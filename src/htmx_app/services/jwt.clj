(ns htmx-app.services.jwt
  (:require [buddy.core.keys :as keys]
            [buddy.sign.jwt  :as jwt]))

(defn load-keypair [{:keys [private-key-path public-key-path passphrase]}]
  {:private (keys/private-key private-key-path passphrase)
   :public  (keys/public-key  public-key-path)})

(defn sign [claims {:keys [private]}]
  (try
    {:ok (jwt/sign claims private {:alg :rs512})}
    (catch Exception e
      {:error (ex-message e)})))

(defn verify [token {:keys [public]}]
  (try
    {:ok (jwt/unsign token public {:alg :rs512})}
    (catch Exception _
      {:error :invalid-token})))
