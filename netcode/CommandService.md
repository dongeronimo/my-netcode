# Command Service

The command service receives incoming UPD packets with command instructions from the clients.
It translates the commands and dispatches them to the correct handlers. 

For example, the CommandService receives the packet VO hello. It asks for each Command if they can 
deal with this packet. HelloCommand can and receives the packet VO and deals with the hello message.

## Tasks
- Disable the old UDP server. - DONE
- Create a UDP Gateway Service that will get the datagrams. - DONE
- UDP Gateway Service authenticates the user. - DONE
- UDP Gateway Service passes the Command VO to the Command Service. - DONE
- Client Connection Object sends UDP to the Gateway.