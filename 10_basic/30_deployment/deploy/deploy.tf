provider "aws" { //snippet:deploy_aws_provider
  region = "eu-central-1"
}//eos:deploy_aws_provider

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
   owners      = [ "137112412989" ]
}

resource "aws_key_pair" "todo_keypair" {
  key_name   = "todo_keypair"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}

resource "aws_security_group" "todo_instance_security_group" {
  name        = "todo_instance_security_group"
  vpc_id         = "${aws_vpc.todo_vpc.id}"

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

resource "aws_instance" "todo_instance" {
  ami              = "${data.aws_ami.amazon_linux2_ami.id}"
  subnet_id        = "${aws_subnet.public_subnet.id}"
  instance_type    = "t2.micro"
  key_name         = "${aws_key_pair.todo_keypair.id}"
  security_groups  = [ "${aws_security_group.todo_instance_security_group.id}" ]
}
