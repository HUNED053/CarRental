// File: src/pages/Payments.jsx

import React, { useEffect, useState, useContext } from "react";
import AuthContext from "../context/AuthContext";
import { getUserPayments } from "../services/paymentService";
import Navbar from "../components/Navbar";

const Payments = () => {
  const { user } = useContext(AuthContext);
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPayments = async () => {
      try {
        const data = await getUserPayments(user.id);
        console.log("Payments API response:", data);
        setPayments(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error("Error fetching payments:", error);
        setPayments([]);
      } finally {
        setLoading(false);
      }
    };
    if (user) fetchPayments();
  }, [user]);

  if (loading) return <p className="loading">Loading payment details...</p>;

  return (
    <div>
      <Navbar />
      <div className="payments-container">
        <h1>Your Payments</h1>
        {payments.length === 0 ? (
          <p className="no-payments">No payments found.</p>
        ) : (
          <div className="payment-list">
            {payments.map((p) => (
              <div key={p.sessionId} className="payment-card">
                <h3 className="session-id">
                  <strong>Payment ID:</strong> {p.sessionId}
                </h3>
                <p><strong>Rental ID:</strong> {p.rentalId}</p>
                <p><strong>Original Amount:</strong> ${p.originalAmount}</p>
                <p>
                  <strong>Discount:</strong> {p.discountPercent}% (−$
                  {p.discountAmount})
                </p>
                <p><strong>Total after Discount:</strong> ${p.amountToPay}</p>
                <p><strong>Type:</strong> {p.type}</p>
                <p>
                  <strong>Status:</strong>{" "}
                  <span
                    className={
                      p.status === "PENDING" ? "pending-status" : "completed-status"
                    }
                  >
                    {p.status}
                  </span>
                </p>
                {p.status === "PENDING" && (
                  <a
                    href={p.sessionUrl}
                    className="btn pay-btn"
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    Complete Payment
                  </a>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Payments;
