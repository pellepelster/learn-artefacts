# snippet:deploy_aws_ami
data "aws_ami" "amazon_linux2_ami" {
  most_recent = true
  name_regex  = "^amzn2-ami-hvm-"
  owners      = ["137112412989"]
}
# /snippet:deploy_aws_ami

# snippet:deploy_aws_key
resource "aws_key_pair" "todo_keypair" {
  key_name   = "todo_keypair"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}
# /snippet:deploy_aws_key

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

# snippet:deploy_aws_instance_systemd_unit
data "template_file" "todo_systemd_service" {
  template = "${file("todo.service.tpl")}"

  vars {
    application_jar = "${var.application_jar}"
  }
}
# /snippet:deploy_aws_instance_systemd_unit

resource "aws_instance" "todo_instance" {

  # snippet:deploy_aws_instance
  ami             = "${data.aws_ami.amazon_linux2_ami.id}"
  subnet_id       = "${aws_subnet.public_subnet.id}"
  instance_type   = "t2.micro"
  key_name        = "${aws_key_pair.todo_keypair.id}"
  security_groups = ["${aws_security_group.todo_instance_ssh_security_group.id}", "${aws_security_group.todo_instance_http_security_group.id}"]
  # /snippet:deploy_aws_instance

  # snippet:deploy_aws_instance_jar
  provisioner "file" {
    source      = "../todo-server/build/libs/${var.application_jar}"
    destination = "${var.application_jar}"

    connection {
      user = "ec2-user"
    }
  }
  # /snippet:deploy_aws_instance_jar

  # snippet:deploy_aws_instance_systemd
  provisioner "file" {
    content     = "${data.template_file.todo_systemd_service.rendered}"
    destination = "todo.service"

    connection {
      user = "ec2-user"
    }
  }
  # /snippet:deploy_aws_instance_systemd

  # snippet:deploy_aws_instance_install
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
      user = "ec2-user"
    }
  }
  # /snippet:deploy_aws_instance_install

}

output "instance_fqdn" {
  value = "${aws_instance.todo_instance.public_dns}"
}
