(ns cweather.api
  (:require [clj-http.client :as client]
    [clojure.data.json :as json]))

; (def api-key "b6907d289e10d714a6e88b30761fae22")
(def api-key "5a043a1bd95bf3ee500eb89de107b41e")

(defn get-weather
  ([] (get-weather "London"))
  ([location] 
    (json/read-str
      (:body 
        (client/get
          (str "https://api.openweathermap.org/data/2.5/find?q=" location "&units=metric&appid=" api-key))))
  )
)

(defn get-temp
  ([] (get-temp "London"))
  ([location]
    (try
      (Thread/sleep 1000)
      (.toString (((((get-weather location) "list") 0) "main") "temp"))
      (catch Exception e (str "Couldn't get weather. Caught exception: " (.getMessage e)))
    )
  )
)

(defn get-name []
  ((((get-weather) "list") 0) "name")
)
