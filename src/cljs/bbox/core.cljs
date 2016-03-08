(ns bbox.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))


;; -------------------------
;; State
(def myinputs (atom [{:id "item1" :x 100 :y 100 :text "Anders"}
                     {:id "item2" :x 200 :y 100 :text "Benny"}
                     {:id "item3" :x 100 :y 200 :text "Kristoffer"}
                     {:id "item4" :x 200 :y 200 :text "Tobias"}]))

;; -------------------------
;; Views

(defn entext [x y text id]
  [:text {:id id :x x :y y :fill "red"} text])

(defn eninput [{:keys [x y text id]}]
  [:g
    [:rect {:x x :y y :width 50 :height 50}]
    [entext x y text id]
  ])

(defn drawinputs []
  (into [:g] (mapv eninput @myinputs)))

(defn ensvg []
  [:svg {:x 0 :y 0 :width 500 :height 500}
    (drawinputs)])

(defn get-width [eid]
  (let [el (.getElementById js/document eid)
        box (.getBBox el)
        width (.-width box)]
        width))

(defn getitemid []
  (let [firstitem (first @myinputs)
        itemid    (:id firstitem)]
    (js/alert (str "Jag ser att första item har id " itemid))))

(defn getitemx []
  (let [firstitem (last @myinputs)
        itemid    (:id firstitem)]
    (js/alert (str "item " itemid " har x: " (get-width itemid)))))

(defn enknapp []
  [:button {:on-click getitemx} "Testa"])

  ; [:button {:on-click (fn [] (js/alert "pang")) } "Testa"])

(defn home-page []
  [:div [:h2 "Welcome to bbox"]
   [:div
     [enknapp]
     [ensvg]
     ]])

(defn about-page []
  [:div [:h2 "About bbox"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
