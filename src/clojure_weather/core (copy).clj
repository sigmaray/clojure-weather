(ns clojure-weather.core (:gen-class)
  (:use clojure-weather.api)
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

; (defmacro log [& body]
;   `(do
;       (println {"(str (java.util.Date.))" (str (java.util.Date.)) "~(meta &form)" ~(meta &form) "'~body" '~body})
;       (do ~@body)))

; (defmacro kv [arg]
;   `{'~arg ~arg})

(defmacro kv [& args]
  `'~(map #(-> [(str %) %]) args))

(defmacro log [& body]
  `(do
      (println [(kv (str (java.util.Date.))) (kv ~(meta &form)) (kv '~body)])
      (do ~@body)))

; (log (+ 1 2))

; (defmacro kv3 [body]
;   `(do
;       (println ~(str body))))
; (kv3 (cat "1" "2"))

; (kv "1")
; (kv2 "1" "2")



(def ticon nil)

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

  ; public void drawCenteredString(String s, int w, int h, Graphics g) {
  ;   FontMetrics fm = g.getFontMetrics();
  ;   int x = (w - fm.stringWidth(s)) / 2;
  ;   int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
  ;   g.drawString(s, x, y);
  ; }
(let g2 (.createGraphics image))

(defn get-tray-image [tz]
  (let [
    image (new BufferedImage 32 32 (BufferedImage/TYPE_INT_ARGB))
    ]
    ; (.setColor g2 Color/white)
    ; (.fillRect g2 0 0 32 32)
    (.setColor g2 Color/black)
    (.setFont g2 (new Font "TimesRoman" Font/PLAIN, 9))
    ; (.drawString g2 (.toString tz) 0 16)
    (draw-centered-text g2 (.toString tz) 32 32)


    ; e.Graphics.DrawString("My String", FontFamily/GenericSansSerif, Brushes/Black, ClientRectangle, TextFormatFlags.HorizontalCenter |  TextFormatFlags.VerticalCenter | TextFormatFlags.GlyphOverhangPadding);

    ; e.Graphics.DrawString("My String", this.Font, Brushes.Black, ClientRectangle, sf);

    ; g.DrawString(letter, new Font(FontFamily.GenericSansSerif, emSize, FontStyle.Regular),
    ;         new SolidBrush(Color.Black), padx, pady);
    ; FontFamily.GenericSansSerif


    image)
  )

; (defn add-tray-icon [_ tz]
;   (let [
;     systemTray (SystemTray/getSystemTray)
;     trayIcon (new TrayIcon (get-tray-image tz) "Weather Tray")
;     ]
;     (.add systemTray trayIcon))
;   )

(defn add-tray-icon [tz]
  (def ticon (new TrayIcon (get-tray-image tz) "Weather Tray"))
  (.add (SystemTray/getSystemTray) ticon))

(defn update-tray-icon [tz]
  (.setImage ticon (get-tray-image tz)))

(defn p-update-tray-icon [_ tz]
  (update-tray-icon tz))

(defn start-tray-cycle-thread []
  (.start (Thread. (fn [] (
    (loop []
      (log
        (update-tray-icon (get-temp "Moscow"))
        (Thread/sleep 1000)
        (recur))))))))

(defn -setup-ui []
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
                  (.setVisible true))
      (add-tray-icon "N/A")
      (start-tray-cycle-thread)))

(defn -main []
  (-setup-ui)
)
