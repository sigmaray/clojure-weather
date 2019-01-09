(ns cweather.core (:gen-class)
  (:use cweather.api))

; (defn foo
;   "I don't do a whole lot."
;   [x]
;   (println x "Hello, World!"))

; (defn -main []
;    (println "I'm a little teapot!"))

(defn -main []
  (prn "Trying to get location... It will take some time")
  (let [location "Madrid"]
    (clojure.pprint/pprint [location (get-temp location)])))
