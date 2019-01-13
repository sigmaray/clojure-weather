(ns cweather.core (:gen-class)
  (:use cweather.api)
  (:import [javax.swing JFrame JLabel JTextField JButton]
          [java.awt.event ActionListener]
          [java.awt GridLayout AWTException Color Graphics2D SystemTray TrayIcon Font]
          [java.awt.image BufferedImage])
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

(def ticon nil)

(defn get-tray-image [tz]
  (let [
    image (new BufferedImage 32 32 (BufferedImage/TYPE_INT_ARGB))
    g2 (.createGraphics image)
    ]
    (.setColor g2 Color/white)
    (.fillRect g2 0 0 32 32)
    (.setColor g2 Color/black)
    (.setFont g2 (new Font "TimesRoman" Font/PLAIN, 9))
    (.drawString g2 tz 0 16)
    image)
  )

; (defn add-tray-icon [_ tz]
;   (let [
;     systemTray (SystemTray/getSystemTray)
;     trayIcon (new TrayIcon (get-tray-image tz) "Weather Tray")
;     ]
;     (.add systemTray trayIcon))
;   )

(defn add-tray-icon [_ tz]
  (def ticon (new TrayIcon (get-tray-image tz) "Weather Tray"))
  (.add (SystemTray/getSystemTray) ticon))

(defn update-tray-icon [tz]
  (.setImage ticon (get-tray-image tz)))

(defn setup-ui []
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
                      (.start (Thread. (fn [] (
                        (. get-button (setEnabled false))
                        (. main-label
                             (setText "...Loading..."))
                        (let [temp (get-temp (. location-text (getText)))]
                          (. get-button (setEnabled true))
                          (. main-label
                             (setText temp))
                          (update-tray-icon temp))))))))))
      (doto frame 
                  (.setDefaultCloseOperation (JFrame/EXIT_ON_CLOSE)) ;uncomment this line to quit app on frame close
                  (.setLayout (new GridLayout 2 2 3 3))
                  (.add location-text)
                  (.add main-label)
                  (.add get-button)
                  (.add temp-label)
                  (.setSize 400 200)
                  (.setVisible true)
                  (add-tray-icon "N/A"))))

(defn -main []
  (setup-ui))
