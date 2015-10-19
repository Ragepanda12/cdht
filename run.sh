xterm -hold -title "Peer 1" -e "java cdht 1 2 3" &
xterm -hold -title "Peer 2" -e "java cdht 2 3 4" &
xterm -hold -title "Peer 3" -e "java cdht 3 4 5" &
xterm -hold -title "Peer 4" -e "java cdht 4 5 1" &
xterm -hold -title "Peer 5" -e "java cdht 5 1 2"
