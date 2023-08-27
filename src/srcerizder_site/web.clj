;; Define Namespace & alias for easy use (bleh, boilerplate...)
(ns srcerizder-site.web
  (:require [ring.middleware.content-type :refer [wrap-content-type]]
            [optimus.assets :as assets]
            [optimus.export]
            [optimus.link :as link]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :refer [serve-live-assets]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup.page :refer [html5]]
            [stasis.core :as stasis]))

;;;;;;;;;;;;;;;;
;; Asset Pull ;;
;;;;;;;;;;;;;;;;

;; Pull down assets for images and styles
(defn get-assets []
  (assets/load-assets "public" [#"/styles/.*"
                                #"/img/.*\.(PNG|GIF|JPG|JPEG|BMP)"]))

;;;;;;;;;;;;;;;;
;; Page Parse ;;
;;;;;;;;;;;;;;;;

;; Define the basic structure of a page
(defn layout-page [request page]
  (html5
    [:head
      [:meta {:charset "utf-8"}]
        [:meta
         {:content "width=device-width, initial-scale=1.0", :name "viewport"}]
          [:title "Srcerizder!"]
          [:link {:href "/styles/main.css", :rel "stylesheet"}]]
    [:body
      (comment "the meat n' potate")
      [:div.body page]
      (comment "hyperlink box")
      [:div.body
       {:class "mesg-container"}
       [:p
        [:b [:a {:href "#top"} "Back to" [:code "$(head -n1)"] "?"]]
        [:br]
        [:b [:a {:href "./index.html"} "Back to" [:code "$HOME"] "?"]]
        [:br]]]
      (comment "closer box")
      [:div.body
        [:center
         [:img {:src "./img/HAPPYGRUV.GIF"}]
         [:h3 "GOOBYE!!!!1!!1"] 
         [:img {:src "./img/GOOBYEGRUV.GIF"}]]]]))

;; Parse partial page html docs
(defn partial-pages [pages]
  (zipmap (keys pages)
          (map #(fn [req] (layout-page req %)) (vals pages))))

;; slurp up those pages bAYBE!
(defn get-pages []
  (stasis/merge-page-sources
   {:public (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")
    :partials (partial-pages (stasis/slurp-directory "resources/partials" #".*\.html$"))}))

;;;;;;;;;;;;;;;
;; ProdStuff ;;
;;;;;;;;;;;;;;;

;; serve!
(def app (-> (stasis/serve-pages get-pages)
             (optimus/wrap get-assets optimizations/all serve-live-assets)
             wrap-content-type))

;; define export info for rendered static page
(def export-dir "dist")

(defn export []
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
