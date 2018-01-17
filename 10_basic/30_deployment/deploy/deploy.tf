resource "aws_vpc" "todo_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
}

resource "aws_subnet" "public_subnet" {
  vpc_id                  = "${aws_vpc.todo_vpc.id}"
  cidr_block              = "10.0.0.1/24"
  availability_zone       = "eu-central-1a"
  map_public_ip_on_launch = true
}

resource "aws_internet_gateway" "todo_internet_gateway" {
  vpc_id = "${aws_vpc.todo_vpc.id}"
}

resource "aws_route_table" "todo_main_route_table" {
  vpc_id = "${aws_vpc.todo_vpc.id}"

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.todo_internet_gateway.id}"
  }
}

resource "aws_main_route_table_association" "todo_main_route_table_association" {
  vpc_id         = "${aws_vpc.todo_vpc.id}"
  route_table_id = "${aws_route_table.todo_main_route_table.id}"
}

data "aws_ami" "amazon_linux2_ami" {
  most_recent = true
  name_regex  = "^amzn2-ami-hvm-"
  owners      = ["137112412989"]
}

resource "aws_key_pair" "todo_keypair" {
  key_name   = "todo_keypair"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}

resource "aws_security_group" "todo_instance_ssh_security_group" {
  name   = "todo_instance_ssh_group"
  vpc_id = "${aws_vpc.todo_vpc.id}"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "todo_instance_http_security_group" {
  name   = "todo_instance_http_security_group"
  vpc_id = "${aws_vpc.todo_vpc.id}"

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "template_file" "todo_systemd_service" {
  template = "${file("todo.service.tpl")}"
  vars {
    application_jar = "${var.application_jar}"
  }
}

resource "aws_instance" "todo_instance" {
  ami             = "${data.aws_ami.amazon_linux2_ami.id}"
  subnet_id       = "${aws_subnet.public_subnet.id}"
  instance_type   = "t2.micro"
  key_name        = "${aws_key_pair.todo_keypair.id}"
  security_groups = [ "${aws_security_group.todo_instance_ssh_security_group.id}", "${aws_security_group.todo_instance_http_security_group.id}" ]

  provisioner "file" {
    content     = "${data.template_file.todo_systemd_service.rendered}"
    destination = "todo.service"
    connection {
      user     = "ec2-user"
    }
  }

  provisioner "file" {
    source     = "../todo-server/build/libs/${var.application_jar}"
    destination = "${var.application_jar}"
    connection {
      user     = "ec2-user"
    }
  }

  provisioner "remote-exec" {
    inline = [
      "sudo yum install -y java",
      "sudo useradd todo",
      "sudo mkdir /todo",
      "sudo mv ~ec2-user/todo.service /etc/systemd/system/",
      "sudo mv ~ec2-user/${var.application_jar} /todo",
      "sudo chmod +x /todo/${var.application_jar}",
      "sudo chown -R todo:todo /todo",
      "sudo systemctl enable todo",
      "sudo systemctl start todo",
    ]
    connection {
      user     = "ec2-user"
    }
  }

}

output "instance_fqdn" {
  value = "${aws_instance.todo_instance.public_dns}"
}
