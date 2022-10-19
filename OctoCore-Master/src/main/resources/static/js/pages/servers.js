function repetitiveFunction() {
    setTimeout(() => {
        updateServers()
        repetitiveFunction()
    }, 5000);
}

repetitiveFunction()

function updateServers() {
    const packet = createPacket("PacketInRequestServerData")
    ws.send(packet)
}

eventBus.on("PacketOutServerData", (data) => {
    /*
    Websocket response:
    "servers": [
    {
        "serverName": "Hub-Dev",
        "lastTick": 1660318659575,
        "recentTps": [
            20.000027053036593,
            20.00000301233379,
            20.000002234466915
        ],
        "players": 0,
        "maxPlayers": 20,
        "onlinePlayers": [],
        "names": [],
        "safelyStopped": false,
        "whitelisted": true,
        "maintenance": false,
        "rpgServer": false,
        "formattedTPS": "20.00"
    }
]
     */
    const servers = data.servers;
    //This response contains all the servers, so we just need to clear the table and add the new servers
    clearTable();
    for (let i = 0; i < servers.length; i++) {
        const server = servers[i];
        addServer(server);
    }

    function clearTable() {
        const table = document.getElementById("table-body");
        //Remove the table from the DOM
        table.parentNode.removeChild(table);
        //Create a new table in the DOM
        const newTable = document.createElement("tbody");
        newTable.id = "table-body";
        document.getElementById("table").appendChild(newTable);
    }

    function addServer(server) {
        const name = server.serverName;
        const players = server.players + '/' + server.maxPlayers;
        const whitelist = server.whitelisted;
        const maintenance = server.maintenance;
        const tps = server.formattedTPS;

        console.log({name, players, whitelist, maintenance, tps});

        const row = document.getElementById("table-body");
        const rowHtml = `<td><span>${name}</span></td><td><span>${players}</span></td><td><span>${whitelist}</span></td><td><span>${maintenance}</span></td><td><span>${tps}</span></td><td>
                <button class="btn btn-primary" onclick="restartServer('${name}')">Restart</button>
            </td>`;
        const element = document.createElement("tr");
        element.innerHTML = rowHtml;
        row.appendChild(element);
    }
})
function restartServer(name) {
    const packet = createPacket("PacketInRestartServer", {server: name})
    console.log("Sending packet: ", {packet, name});
    ws.send(packet)
}
