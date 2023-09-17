(ns srcerizder-site.page
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [hiccup.core :refer [html]]))

;; Define the basic structure of a page
(def hiccup-html
  (html
   [:head
    [:meta {:charset "utf-8"}]
    [:meta
     {:content "width=device-width, initial-scale=1.0", :name "viewport"}]
    [:title "Srcerizder!"]
    [:link {:href "/styles/main.css", :rel "stylesheet"}]]
   (comment "the meat n' potate")
   [:header
    [:div
     {:class "title-container"}
     [:h1 "Srcerizder&#39;s Homepage"]
     [:h3 "(Or Something)"]]]
   [:section
    {:id "summary-container"}
    [:p
     [:b "SUM INFOZ:"]
     [:br]
     [:br]
     [:b "&gt;&gt; I&#39;m Currently Learning"]
     ": Common Lisp, Clojure, &amp; StumpWM"
     [:br]
     [:b "&gt;&gt; Ask Me About"]
     ": OpenBSD, Gentoo, Ricing *NIX, Music Theory, &amp; Calculators"
     [:br]
     [:b "&gt;&gt; I Love"]
     ": Music Theory, Being Queer, Tech"
     [:i "(duh)"]
     ", &amp; Linguistics"
     [:br]
     [:b "&gt;&gt; Fun Fact"]
     ": I"
     [:i "will not"]
     "help you if you don&#39;t put in the effort"
     [:b "first"]
     "."
     [:br]]]
   [:section
    {:id "content-container"}
    [:div
     {:class "list-container"}
     [:h4 "Personal Stuffs"]
     [:ul
      [:li
       [:a
        {:rel "me", :href "https://en.pronouns.page/@izder456"}
        "Pronouns"]]
      [:li
       [:a {:rel "me", :href "https://izder456.tumblr.com/"} "Tumblr"]]
      [:li
       [:a
        {:rel "me", :href "mailto:izder456@disroot.org?subject=Hi"}
        "eMail"]]]]
    [:div
     {:class "list-container"}
     [:h4 "Music Stuffs"]
     [:ul
      [:li
       [:a
        {:rel "me", :href "https://soundcloud.com/izder456"}
        "Soundcloud"]]
      [:li
       [:a
        {:rel "me", :href "https://izder456.bandcamp.com/"}
        "Bandcamp"]]]]
    [:div
     {:class "list-container"}
     [:h4 "Social Stuffs"]
     [:ul
      [:li
       [:a
        {:rel "me", :href "https://bsky.app/profile/izder456.bsky.social"}
        "Bsky.app"]]
      [:li
       [:a
        {:rel "me"
         :href "https://mastodon.unixhideout.solutions/@izder456"}
        "Mastodon"]]
      [:li
       [:a
        {:rel "me", :href "https://libranet.de/profile/izder456"}
        "Friendi.ca"]]]]
    [:div
     {:class "list-container"}
     [:h4 "Tech Stuffs"]
     [:ul
      [:li [:a {:rel "me", :href "https://github.com/izder456"} "Github"]]
      [:li
       [:a
        {:rel "me", :href "https://unixhideout.solutions/gl"}
        "GeekLog I Co-Moderate"]]
      [:li
       [:a
        {:rel "me", :href "https://unixhideout.solutions"}
        "Group I Co-Moderate"]]]]]
   [:section
    {:id "mesg-container"}
    [:p
     [:b [:a {:href "#top"} "back to" [:code "$(head -n1)"] "?"]] [:br]
     [:b [:a {:href "./index.html"} "back to" [:code "$home"] "?"]] [:br]]]
   (comment "closer box")
   [:footer
    [:center
     [:img {:src "./img/srcerizder.png"}]
     [:h3 "goobye!!!!1!!1"]
     [:img {:src "./img/goobyegruv.png"}]]]))
