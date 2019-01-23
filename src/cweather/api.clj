(ns cweather.api
  (:require [clj-http.client :as client]
    [clojure.data.json :as json]))

; (def api-key "b6907d289e10d714a6e88b30761fae22")
(def api-key "5a043a1bd95bf3ee500eb89de107b41e")

(defn get-temp
  ([] (get-temp "London"))
  ([location]
    (try
      ; (Thread/sleep 1000)
      (.toString (((((get-weather location) "list") 0) "main") "temp"))
      ; (.toString -45)
      (catch Exception e (prn "Couldn't get weather. Exception caught: " (.getMessage e)) "")
    )
  )
)
