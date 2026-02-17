# OctoCore-1.21
before building OctoCore: 
 * load libs/ with `agile-gui, octospigot-api-1.21`
 * build Commander from source and push to local maven

# OctoCore-velocity
The Velocity project handles the following Redis packets to maintain network state:

### Server Status
Sent periodically by all servers (including the proxy) to report their current status.
- **Type**: `SERVER_DATA`
- **Payload**:
```json
{
  "type": "SERVER_DATA",
  "data": {
    "name": "survival",
    "onlinePlayers": [
      {"uuid": "...", "name": "..."}
    ],
    "maxPlayers": 20,
    "lastTick": 1771364909997,
    "whitelisted": false,
    "tps1": 19.99,
    "tps2": 19.99,
    "tps3": 19.99,
    "maintenance": false
  }
}
```

### Server Life-cycle
Sent when a server starts or shuts down.
- **Types**: `SERVER_ONLINE`, `SERVER_OFFLINE`
- **Payload**:
```json
{
  "type": "SERVER_ONLINE",
  "data": {
    "server": "survival"
  }
}
```

### Staff Alerts
Sent when staff members interact with the network.
- **Types**: `STAFF_CONNECT`, `STAFF_LEAVE`, `STAFF_SWITCH`
- **Payload (Connect/Leave)**:
```json
{
  "type": "STAFF_CONNECT",
  "data": {
    "name": "AdminName",
    "server": "survival",
    "vanishPriority": 10,
    "vanished": false
  }
}
```
- **Payload (Switch)**:
```json
{
  "type": "STAFF_SWITCH",
  "data": {
    "name": "AdminName",
    "from": "survival",
    "to": "hub"
  }
}
```

### Staff Communication
Shared chat channels for staff members.
- **Types**: `STAFF_CHAT`, `ADMIN_CHAT`
- **Payload**:
```json
{
  "type": "STAFF_CHAT",
  "data": {
    "name": "StaffName",
    "coloredName": "&cStaffName",
    "server": "survival",
    "message": "Hello staff!",
    "uuid": "...",
    "sentAt": 1771364909997
  }
}
```

### Network Summary
Sent periodically by the Velocity proxy to correlate stats from all servers.
- **Type**: `NETWORK_SUMMARY`
- **Payload**:
```json
{
  "type": "NETWORK_SUMMARY",
  "data": {
    "totalPlayers": 150,
    "serverPlayerCounts": {
      "hub": 20,
      "survival": 100,
      "creative": 30
    },
    "timestamp": 1771364909997
  }
}
```