(defproject rivulet "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1885"]
                 [enfocus "2.0.0-beta1"]]
  :source-paths ["src/clj"]
  :resource-paths ["src/js" "src/rb"]
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-vertx "0.1.0"]]
  :profiles {:provided
             {:dependencies [[io.vertx/clojure-api "0.3.0"]]}}
  :cljsbuild {:crossovers [rivulet.config]
              :builds [{:source-paths ["src/cljs"]
                        :compiler {:output-to "resources/client.js"}
                        :crossover-path "target/crossover-cljs"}]}
  :vertx {:main rivulet.init/init})
