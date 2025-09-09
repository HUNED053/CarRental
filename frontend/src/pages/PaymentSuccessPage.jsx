import React, { useEffect, useState } from "react";
import { useLocation, Link } from "react-router-dom";
import axios from "../services/api";

const PaymentSuccessPage = () => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const sessionId = searchParams.get("session_id");
  const [notified, setNotified] = useState(false);

  useEffect(() => {
    if (sessionId && !notified) {
      axios.get(`/payments/success?session_id=${sessionId}`)
        .then((response) => {
          console.log("Backend notified:", response.data);
          setNotified(true);
        })
        .catch((error) => {
          console.error("Error notifying backend:", error);
        });
    }
  }, [sessionId, notified]);

  return (
    <div className="payment-success-page">
      <h1>Payment Successful</h1>
      <p>Your payment was successfully processed.</p>
      {sessionId && <p>Session ID: {sessionId}</p>}
      <Link to="/">Return Home</Link>
    </div>
  );
};

export default PaymentSuccessPage;