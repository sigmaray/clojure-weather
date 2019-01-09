(ns cweather.core (:gen-class)
  (:use cweather.api)
  (:import [javax.swing JFrame JLabel JTextField JButton]
          [java.awt.event ActionListener]
          [java.awt GridLayout])
)

; (defn foo
;   "I don't do a whole lot."
;   [x]
;   (println x "Hello, World!"))

; (defn -main []
;    (println "I'm a little teapot!"))

; (defn -main []
;   (prn "Trying to get location... It will take some time")
;   (let [location "Madrid"]
;     (clojure.pprint/pprint [location (get-temp location)])))

(defn -main []
  (let [frame (new JFrame "Weather App")
        location-text (new JTextField "New York")
        main-label (new JLabel "")
        get-button (new JButton "Get Weather")
        temp-label (new JLabel "")
        ]
      (. get-button
          (addActionListener
             (proxy [ActionListener] []
                  (actionPerformed [evt]
                      (let [temp (get-temp (. location-text (getText)))]
                        (. main-label
                           (setText temp)))))))
      (doto frame 
                  ;(.setDefaultCloseOperation (JFrame/EXIT_ON_CLOSE)) ;uncomment this line to quit app on frame close
                  (.setLayout (new GridLayout 2 2 3 3))
                  (.add location-text)
                  (.add main-label)
                  (.add get-button)
                  (.add temp-label)
                  (.setSize 400 200)
                  (.setVisible true)))
)
