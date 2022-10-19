connectWebsocket()
var ws;
function connectWebsocket() {
    let protocol = "wss://";
    if (location.protocol !== 'https:') {
        protocol = "ws://";
    }
    const url = protocol + window.location.host + "/api/ws";
    console.log({url})
    ws = new RobustWebSocket(url);
    ws.addEventListener('open', function () {
        console.log('Connected to websocket');
        auth(ws)
        eventBus.dispatch('connect', null);
    })

// @ts-ignore
    ws.addEventListener('message', event => {
        const packet = JSON.parse(event.data);
        console.log("[WS-PACKET]: ", packet);
        eventBus.dispatch(packet.name, packet.data);
        eventBus.dispatch('packet', packet);
    })
    ws.addEventListener('close', () => {
        console.log('Disconnected from websocket');
        eventBus.dispatch('disconnect', null);
    })

    function auth(sock) {
        const cookie = getJSessionId();
        console.log({cookie})
        if (cookie === "") {
            console.log("No cookie found");
            sock.close();
            return
        }
        const packet = createPacket('PacketInAuthorize', {
            token: cookie
        })
        sock.send(packet)
        const toLog = packet; //packet.replace(cookie,"[REDACTED]");
        console.log("Auth Packet Sent", toLog)
    }
}

function createPacket(name, data) {
    const json = {
        'name': name,
        'play': "IN",
        'data': data
    }
    return JSON.stringify(json);
}
