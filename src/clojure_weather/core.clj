(ns clojure-weather.core
  (:gen-class)
  (:import 
          [javax.swing JFrame JLabel JTextField JButton]
          [java.awt.event ActionListener]
          [java.awt GridLayout AWTException Color Graphics2D SystemTray TrayIcon Font PopupMenu MenuItem]
          [java.awt.image BufferedImage])
  (:require
          [clj-http.client :as client]
          [clojure.data.json :as json]))

(def CITY "Berlin")
(def TRAY-SIZE 24)
(def API-KEY "5a043a1bd95bf3ee500eb89de107b41e")

(defmacro to-hash
  "Convert expression into hasmap.
  Example:
  => (to-hash (+ 1 2))
  {(+ 1 2) 3}"
  [arg] `{'~arg ~arg}) 

(defmacro log
  "Log expression to console and execute it.
  Example:
  => (log (+ 1 2))
  [#inst \"2019-01-23T09:02:52.479-00:00\" {:line 1, :column 1} ((+ 1 2))]
  3"
  [& body]
  `(do
      (println [(java.util.Date.) ~(meta &form) '~body])
      (do ~@body)))

(defn get-temp
  ([] (get-temp "Minsk"))
  ([location]
    (let [url (str "https://api.openweathermap.org/data/2.5/find?q=" location "&units=metric&appid=" API-KEY)]
      (try
        (str
          (-> (client/get url) (get :body) (json/read-str) (get "list") (get 0) (get "main") (get "temp") (str)))
      (catch Exception e
        (prn "Couldn't get weather. Exception caught: " (.getMessage e))
        "N/A")))))

(defn draw-centered-text [g s w h]
  (let [
         fm  (.getFontMetrics g)
         x (int (/ (- w (.stringWidth fm s)) 2))
         y (int (+ (.getAscent fm) (/ (- h (+ (.getAscent fm) (.getDescent fm))) 2)))]
    (.drawString g s x y)))

(defn draw-image [text]
  (let [
         image (new BufferedImage TRAY-SIZE TRAY-SIZE (BufferedImage/TYPE_INT_ARGB))
         g (.createGraphics image)
       ]
    ; (.setColor g Color/white)
    ; (.fillRect g 0 0 32 32)
    (.setColor g Color/black)
    (.setFont g (new Font "DejaVu Sans" Font/PLAIN, 12))
    (draw-centered-text g (.toString text) TRAY-SIZE TRAY-SIZE)
    image))

(defn add-tray-icon [text]
  (let [ticon (new TrayIcon (draw-image text) "Weather Tray")]
    (.add (SystemTray/getSystemTray) ticon)
    ticon))

(defn update-tray-icon [ticon text]
  (.setImage ticon (draw-image text)))

(defn weather-loop [ticon]
  (.start (Thread. (fn [] (
    (loop []
      (log
        (update-tray-icon ticon (get-temp CITY))
        (Thread/sleep (* 1000 60))
        (recur))))))))

(defn menu-item [label callback]
  (let [
         menu (MenuItem. label)
         listener (proxy [ActionListener] [] (actionPerformed [event] (callback)))]
    (.addActionListener menu listener)
    menu))

(defn -main []
  (let [
         frame (new JFrame "Clojure Weather App")
         popup (PopupMenu.)
         ticon (add-tray-icon "N/A")]
    (.add popup (menu-item "Exit" #(System/exit 0)))
    (.setPopupMenu ticon popup)
    (weather-loop ticon)))
