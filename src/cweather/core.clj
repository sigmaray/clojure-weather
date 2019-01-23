(ns cweather.core
  (:gen-class)
  (:import [javax.swing JFrame JLabel JTextField JButton]
          [java.awt.event ActionListener]
          [java.awt GridLayout AWTException Color Graphics2D SystemTray TrayIcon Font PopupMenu MenuItem]
          [java.awt.image BufferedImage])
  (:require
          [clj-http.client :as client]
          [clojure.data.json :as json]))

(def CITY "Minsk")
(def TRAY-SIZE 24)

(def API-KEY "5a043a1bd95bf3ee500eb89de107b41e")

(defn get-weather
  ([] (get-weather "London"))
  ([location] 
    (json/read-str
      (:body 
        (client/get
          (str "https://api.openweathermap.org/data/2.5/find?q=" location "&units=metric&appid=" API-KEY))))
  )
)

(defn get-temp
  ([] (get-temp "London"))
  ([location]
    (try
      (.toString (((((get-weather location) "list") 0) "main") "temp"))
      (catch Exception e (prn "Couldn't get weather. Exception caught: " (.getMessage e)) "")
    )
  )
)

(defmacro kv [& args]
  `'~(map #(-> [(str %) %]) args))

(defmacro log [& body]
  `(do
      (println [(kv ~(str (java.util.Date.))) (kv ~(meta &form)) (kv '~body)])
      (do ~@body)))

(def ticon nil)

(defn draw-centered-text [g s w h]
  (let [
    fm  (.getFontMetrics g)
    x (int (/ (- w (.stringWidth fm s)) 2))
    y (int (+ (.getAscent fm) (/ (- h (+ (.getAscent fm) (.getDescent fm))) 2)))]
    (.drawString g s x y)
  )
)

(defn get-tray-image [tz]
  (let [
    image (new BufferedImage TRAY-SIZE TRAY-SIZE (BufferedImage/TYPE_INT_ARGB))
    g2 (.createGraphics image)
    ]
    ; (.setColor g2 Color/white)
    ; (.fillRect g2 0 0 32 32)
    (.setColor g2 Color/black)
    (.setFont g2 (new Font "DejaVu Sans" Font/PLAIN, 12))
    (draw-centered-text g2 (.toString tz) TRAY-SIZE TRAY-SIZE)
    image)
  )

(defn add-tray-icon [tz]
  (def ticon (new TrayIcon (get-tray-image tz) "Weather Tray"))
  (.add (SystemTray/getSystemTray) ticon))

(defn update-tray-icon [tz]
  (.setImage ticon (get-tray-image tz)))

(defn start-tray-cycle-thread []
  (.start (Thread. (fn [] (
    (loop []
      (log
        (update-tray-icon (get-temp CITY))
        (Thread/sleep (* 1000 60))
        (recur))))))))

(defn menu-item [label callback]
  (let [menu (MenuItem. label)
        listener (proxy [ActionListener] []
                   (actionPerformed [event] (callback)))]
    (.addActionListener menu listener)
    menu))

(defn -main []
  (let [frame (new JFrame "Clojure Weather App")]
    (add-tray-icon "N/A")
    (let [popup (PopupMenu.)]
      (.add popup (menu-item "Exit" #(System/exit 0)))
      (.setPopupMenu ticon popup))
    (start-tray-cycle-thread)))

