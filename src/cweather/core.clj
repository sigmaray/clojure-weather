(ns cweather.core (:gen-class)
  (:use cweather.api)
  (:import [javax.swing JFrame JLabel JTextField JButton]
          [java.awt.event ActionListener]
          [java.awt GridLayout AWTException Color Graphics2D SystemTray TrayIcon Font]
          [java.awt.image BufferedImage])
)

(defmacro kv [& args]
  `'~(map #(-> [(str %) %]) args))

(defmacro log [& body]
  `(do
      (println [(kv (str (java.util.Date.))) (kv ~(meta &form)) (kv '~body)])
      (do ~@body)))

(def ticon nil)

; public void drawCenteredString(String s, int w, int h, Graphics g) {
;   FontMetrics fm = g.getFontMetrics();
;   int x = (w - fm.stringWidth(s)) / 2;
;   int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
;   g.drawString(s, x, y);
; }
(defn draw-centered-text [g s w h]
  (let [
    fm  (.getFontMetrics g)
    x (int (/ (- w (.stringWidth fm s)) 2))
    y (int (+ (.getAscent fm) (/ (- h (+ (.getAscent fm) (.getDescent fm))) 2)))
    ]
    (prn [w (.stringWidth fm s)])
    (prn [(.getAscent fm) h (.getAscent fm) (.getDescent fm)])
    (prn [g s x y])
    (.drawString g s x y)
  )
)

(defn get-tray-image [tz]
  (let [    
    image (new BufferedImage 24 24 (BufferedImage/TYPE_INT_ARGB))
    g2 (.createGraphics image)
    ]
    ; (.setColor g2 Color/white)
    ; (.fillRect g2 0 0 32 32)
    (.setColor g2 Color/black)
    (.setFont g2 (new Font "TimesRoman" Font/PLAIN, 9))
    ; (.drawString g2 (.toString tz) 0 16)
    (draw-centered-text g2 (.toString tz) 24 24)

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
        (update-tray-icon (get-temp "Moscow"))
        (Thread/sleep 1000)
        (recur))))))))

(defn -setup-ui []
  (let [
        frame (new JFrame "Weather App")
        ; location-text (new JTextField "New York")
        ; main-label (new JLabel "")
        ; get-button (new JButton "Get Weather")
        ; temp-label (new JLabel "")
        ]
      ; (. get-button
      ;     (addActionListener
      ;        (proxy [ActionListener] []
      ;             (actionPerformed [evt]
      ;                 (.start (Thread. (fn [] (
      ;                   (. get-button (setEnabled false))
      ;                   (. main-label
      ;                        (setText "...Loading..."))
      ;                   (let [temp (get-temp (. location-text (getText)))]
      ;                     (. get-button (setEnabled true))
      ;                     (. main-label
      ;                        (setText temp))
      ;                     (update-tray-icon temp))))))))))
      (doto frame 
                  ; (.setDefaultCloseOperation (JFrame/EXIT_ON_CLOSE)) ;uncomment this line to quit app on frame close
                  ; (.setLayout (new GridLayout 2 2 3 3))
                  ; (.add location-text)
                  ; (.add main-label)
                  ; (.add get-button)
                  ; (.add temp-label)
                  ; (.setSize 400 200)
                  ; (.setVisible true)
                  )
      (add-tray-icon "N/A")
      (start-tray-cycle-thread)))

(defn -main []
  (-setup-ui)
)
