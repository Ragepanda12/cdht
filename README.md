# cdht
CS3311 Computer Networks Assignment 2015
Circular DHT Program to create a P2P network where each Peer knows the location of the next Peer.
Each peer also 'holds' a file which can be requested from any other peer in the network.
E.g Peer 2's next peer is 5, which has item 2. If I ask for item 2 in Peer 2's window, it should tell me that peer 5 has the item.

Also, if any of the windows are closed, the circular DHT should reform itself by detecting that the network has been broken and then redirecting peers as required.
