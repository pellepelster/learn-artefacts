[Unit]
Description=todo application
After=syslog.target

[Service]
User=todo
ExecStart=/todo/${var.application_jar}
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
