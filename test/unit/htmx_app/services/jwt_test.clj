(ns htmx-app.services.jwt-test
  (:require [clojure.test          :refer [deftest is testing use-fixtures]]
            [htmx-app.services.jwt :as svc])
  (:import [java.security KeyPairGenerator]))

(def ^:private test-keypair (atom nil))

(defn- gen-keypair-fixture [f]
  (let [gen (doto (KeyPairGenerator/getInstance "RSA") (.initialize 2048))
        kp  (.generateKeyPair gen)]
    (reset! test-keypair {:private (.getPrivate kp)
                          :public  (.getPublic kp)}))
  (f))

(use-fixtures :once gen-keypair-fixture)

(deftest sign
  (testing "returns {:ok token} for valid claims"
    (let [result (svc/sign {:id 1 :email "a@b.com" :role "user"} @test-keypair)]
      (is (contains? result :ok))
      (is (string? (:ok result)))))

  (testing "token is a non-empty string"
    (let [{:keys [ok]} (svc/sign {:id 42} @test-keypair)]
      (is (seq ok)))))

(deftest verify
  (testing "valid token returns {:ok claims}"
    (let [claims  {:id 1 :email "a@b.com" :role "user"}
          token   (:ok (svc/sign claims @test-keypair))
          result  (svc/verify token @test-keypair)]
      (is (contains? result :ok))
      (is (= 1    (get-in result [:ok :id])))
      (is (= "a@b.com" (get-in result [:ok :email])))))

  (testing "tampered token returns {:error :invalid-token}"
    (is (= {:error :invalid-token}
           (svc/verify "not.a.valid.token" @test-keypair))))

  (testing "nil token returns {:error :invalid-token}"
    (is (= {:error :invalid-token}
           (svc/verify nil @test-keypair)))))
