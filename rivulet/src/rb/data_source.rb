require 'vertx'

#data = File.read('/usr/share/dict/words').split
data =  File.read('data/words').split

Vertx.set_periodic(100) do
  Vertx::EventBus.publish(Vertx.config["stream-address"], data.sample)
end
