require 'vertx'

data = File.read('/usr/share/dict/words').split

Vertx.set_periodic(100) do
  Vertx::EventBus.publish(Vertx.config["stream-address"], data.sample)
end
