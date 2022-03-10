(ns core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defprotocol Drawable
  (move [drawable])
  (render [drawable]))

(defrecord Particle [x y size x-dir y-dir])

(defn- update-direction
  [{:keys [x y x-dir y-dir] :as particle}]
  (if (> (q/dist x y 0 0) 250)
    (let [new-x-dir (/ (- (q/random -250 250)
                          x-dir)
                       600)
          new-y-dir (/ (- (q/random -250 250)
                          y-dir)
                       600)]
      (assoc particle :x-dir new-x-dir :y-dir new-y-dir))
    particle))

(extend-type Particle
  Drawable
  (move [{:keys [x y] :as particle}]
        (let [{:keys [x-dir y-dir] :as new-direction-particle} (update-direction particle)]
          (assoc new-direction-particle :x (+ x x-dir) :y (+ y y-dir))))
  (render [{:keys [x y size]}]
    (q/fill 238 120 138 (* size 30))
    (q/ellipse x y size size)))

(defn- initialise
  []
  (q/smooth)
  (q/no-stroke)
  (repeatedly 4000 #(->Particle 0 0 (q/random 0.5 4) (q/random -1 1) (q/random -1 1))))

(defn- draw
  [state]
  (q/background 35 27 107)
  (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
    (dorun (map render state))))

(defn- update-state
  [state]
  (map move state))

(q/defsketch sketch
             :title "3.1.2"
             :setup initialise
             :update update-state
             :draw draw
             :middleware [m/fun-mode]
             :size [600 600])