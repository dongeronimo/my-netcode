# Command Service

The command service receives incoming UPD packets with command instructions from the clients.
It translates the commands and dispatches them to the correct handlers. 

## Tasks
- Disable the old UDP server.
- Create a UDP Gateway Service that will get the datagrams.
- UDP Gateway Service authenticates the user.
- UDP Gateway Service passes the Command VO to the Command Service.
- Client Connection Object sends UDP to the Gateway.