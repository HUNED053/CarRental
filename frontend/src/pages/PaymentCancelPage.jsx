import React from "react";
import { Link } from "react-router-dom";

const PaymentCancelPage = () => {
  return (
    <div className="payment-cancel-page">
      <h1>Payment Canceled</h1>
      <p>Your payment was canceled. Please try again later.</p>
      <Link to="/">Return Home</Link>
    </div>
  );
};

export default PaymentCancelPage;