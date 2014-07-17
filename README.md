Sheepland
=========
This is sheepland board game written in java for the 2014 Politecnico of Milan final test of computer Engineering.
Thanks to Matteo Basilico who was my partner for the project.

So here is a quick explanation:

Server will manage both RMI and Socket connections. Clients can play via CLI, static UI or dynamic UI (well, sort of...).

Disconnection is implemented and managed everywhere except during the creation of the match (ie while server is waiting for 4 players to begin the 
match, and while clients are choosing where to put their pastor).
Reconnection is implemented, and its only bug appears when 2 clients disconnect; then, when one wants to join the match again, it won't be asked whom 
of the 2 disconnected players he was.

UI is very basic.
The entire application is in italian language (sorry about that).

![Alt text](sheepland.png?raw=true)

