var vertx = require('vertx')
var config = require('vertx/container').config

var server = vertx.createHttpServer()

server.requestHandler(function(req) { 
    req.response.sendFile('resources/' + 
                          (req.path() == '/' ? 'index.html' : 
                                               req.path()))
})

vertx.
    createSockJSServer(server).
    bridge({prefix: '/eventbus'}, 
           [{address: config['command-address']}], // client -> server
           [{address: config['stream-address']},   // server -> client
            {address: config['result-address']},
            {address: config['stats-address']}]) 

server.listen(8080, 'localhost')
