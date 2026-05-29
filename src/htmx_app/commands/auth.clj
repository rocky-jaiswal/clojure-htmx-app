(ns htmx-app.commands.auth
  (:require [htmx-app.services.user     :as user-service]
            [htmx-app.services.jwt      :as jwt-svc]
            [htmx-app.commands.pipeline :as pipe]
            [malli.core                 :as m]))

(defn register! [ds keypair {:keys [email password] :as params}]
  (-> {:ok params}
      (pipe/valid #(user-service/validate-registration params))
      (pipe/valid #(when (user-service/find-by-email ds email) "Email already registered"))
      (pipe/then  (fn [_]
                    (let [user (user-service/create! ds {:email email :password password})]
                      {:ok {:id    (:users/id user)
                            :email (:users/email user)
                            :role  (:users/role user)}})))
      (pipe/then  #(jwt-svc/sign % keypair))))

(defn login [ds keypair {:keys [email password]}]
  (let [user (user-service/find-by-email ds email)]
    (if (and user (user-service/verify-password user password))
      (jwt-svc/sign {:id    (:users/id user)
                     :email (:users/email user)
                     :role  (:users/role user)}
                    keypair)
      {:error "Invalid email or password"})))

(def ^:private credentials
  [:map [:email :string] [:password :string]])

(m/=> register! [:=> [:cat :any :any (conj credentials [:confirm-password :string])] pipe/Result])
(m/=> login     [:=> [:cat :any :any credentials] pipe/Result])
