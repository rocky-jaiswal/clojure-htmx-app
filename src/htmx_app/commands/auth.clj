(ns htmx-app.commands.auth
  (:require [htmx-app.services.user :as user-service]
            [clojure.string         :as str]))

(defn register! [ds {:keys [email password confirm-password]}]
  (cond
    (str/blank? email)               {:error "Email is required"}
    (> (count email) 254)            {:error "Email must be 254 characters or less"}
    (str/blank? password)            {:error "Password is required"}
    (< (count password) 8)           {:error "Password must be at least 8 characters"}
    (> (count password) 72)          {:error "Password must be 72 characters or less"}
    (not= password confirm-password) {:error "Passwords do not match"}
    (user-service/find-by-email ds email) {:error "Email already registered"}
    :else (let [user (user-service/create! ds {:email email :password password})]
            {:ok {:id    (:users/id user)
                  :email (:users/email user)
                  :role  (:users/role user)}})))

(defn login [ds {:keys [email password]}]
  (let [user (user-service/find-by-email ds email)]
    (if (and user (user-service/verify-password user password))
      {:ok {:id    (:users/id user)
            :email (:users/email user)
            :role  (:users/role user)}}
      {:error "Invalid email or password"})))
