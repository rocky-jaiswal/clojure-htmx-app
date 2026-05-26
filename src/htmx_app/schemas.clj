(ns htmx-app.schemas
  (:require [clojure.string :as str]))

(def name-schema
  [:and
   [:fn {:error/message "Name cannot be blank"} #(not (str/blank? %))]
   [:fn {:error/message "Name must be 100 characters or less"} #(<= (count %) 100)]])

(def title-schema
  [:and
   [:fn {:error/message "Title cannot be blank"} #(not (str/blank? %))]
   [:fn {:error/message "Title must be 200 characters or less"} #(<= (count %) 200)]])

(def registration-schema
  [:and
   [:fn {:error/message "Email is required"}
    (fn [{:keys [email]}] (not (str/blank? email)))]
   [:fn {:error/message "Email must be 254 characters or less"}
    (fn [{:keys [email]}] (<= (count email) 254))]
   [:fn {:error/message "Email must contain @"}
    (fn [{:keys [email]}] (or (str/blank? email) (str/includes? email "@")))]
   [:fn {:error/message "Password is required"}
    (fn [{:keys [password]}] (not (str/blank? password)))]
   [:fn {:error/message "Password must be at least 8 characters"}
    (fn [{:keys [password]}] (>= (count password) 8))]
   [:fn {:error/message "Password must be 72 characters or less"}
    (fn [{:keys [password]}] (<= (count password) 72))]
   [:fn {:error/message "Password must contain at least one number"}
    (fn [{:keys [password]}] (or (str/blank? password) (boolean (re-find #"\d" password))))]
   [:fn {:error/message "Password must contain at least one special character"}
    (fn [{:keys [password]}] (or (str/blank? password) (boolean (re-find #"[^a-zA-Z0-9]" password))))]
   [:fn {:error/message "Passwords do not match"}
    (fn [{:keys [password confirm-password]}] (= password confirm-password))]])
