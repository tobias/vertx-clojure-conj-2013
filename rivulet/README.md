# Rivulet 

Sample polyglot stream processing with Vert.x

The version used in the presentation used redis and
`/usr/share/dict/words`. This version uses an atom for storage and a
words dict in `data/words` to make it easier to get started with. If
you do have redis available, you can use it by swapping the init fns
in `src/clj/rivulet/stats.clj`.

First, compile the clojurescript with `lein cljsbuild once`, then run
with `lein vertx run` and point your browser at
<http://localhost:8080>.
