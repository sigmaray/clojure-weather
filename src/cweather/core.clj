(ns cweather.core (:gen-class)
  (:require [clj-http.client :as client]
    [clojure.data.json :as json]))

; (def api-key "b6907d289e10d714a6e88b30761fae22")
(def api-key "5a043a1bd95bf3ee500eb89de107b41e")

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn get-weather
  ([] (get-weather "London"))
  ([location] 
    (json/read-str
      (:body 
        (client/get
          (str "https://api.openweathermap.orgg/data/2.5/find?q=" location "&units=metric&appid=" api-key))))
  )
)

(defn get-temp
  ([] (get-temp "London"))
  ([location]
    (try
      (Thread/sleep 4000)
      (((((get-weather location) "list") 0) "main") "temp")
      (catch Exception e (str "Couldn't get weather. Caught exception: " (.getMessage e)))
    )    
  )
)

(defn get-name []
  ((((get-weather) "list") 0) "name")
)

; (defn -main []
;    (println "I'm a little teapot!"))

(defn -main []
  (prn "Trying to get location... It will take some time")
  (let [location "Madrid"]
    (clojure.pprint/pprint [location (get-temp location)])))
