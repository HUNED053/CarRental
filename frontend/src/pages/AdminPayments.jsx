import React, { useEffect, useState } from 'react';
import axios from 'axios';
import * as XLSX from 'xlsx';
import Navbar from '../components/Navbar';
import { toast } from "react-toastify";
import "../App.css";

const AdminPayments = () => {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPayments = async () => {
      try {
        const { data } = await axios.get('http://localhost:8080/payments/');
        setPayments(data);
      } catch {
        setError('Failed to fetch payments');
      } finally {
        setLoading(false);
      }
    };
    fetchPayments();
  }, []);

  const exportToExcel = () => {
    const rows = payments.map(p => {
      const u = p.rental.user;
      const c = p.rental.car;
      return {
        'User ID': u.id,
        'User Name': `${u.firstName} ${u.lastName}`,
        'Car': `${c.brand} ${c.model}`,
        'Amount Paid': p.amountToPay,
        'Rental Period': `${new Date(p.rental.rentalDate).toLocaleDateString()} — ${new Date(p.rental.returnDate).toLocaleDateString()}`,
        'Payment Status': p.status,
      };
    });

    const ws = XLSX.utils.json_to_sheet(rows);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Payments');
    XLSX.writeFile(wb, 'admin_payments.xlsx');
  };

  const togglePaymentStatus = async (sessionId) => {
    try {
      const res = await axios.patch(`http://localhost:8080/payments/${sessionId}/toggle-status`);
      setPayments(payments.map(payment =>
        payment.sessionId === sessionId ? res.data : payment
      ));
    } catch (error) {
      console.error("Failed to toggle payment status", error);
    }
  };
  

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="admin-payment-container">
      <Navbar />

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem 0' }}>
        <h1>Admin Payment Management</h1>
        <button onClick={exportToExcel} className="btn" style={{ width: 'auto' }}>
          Export to Excel
        </button>
      </div>

      {payments.length === 0 ? (
        <p className="no-data">No payments available</p>
      ) : (
        <table className="admin-payment-table">
          <thead>
            <tr>
              <th>User ID</th>
              <th>User Name</th>
              <th>Car</th>
              <th>Amount Paid</th>
              <th>Rental Period</th>
              <th>Payment Status(Toggle to Change)</th>
            </tr>
          </thead>
          <tbody>
            {payments.map(p => {
              const u = p.rental.user;
              const c = p.rental.car;
              return (
                <tr key={p.sessionId}>
                  <td>{u.id}</td>
                  <td>{u.firstName} {u.lastName}</td>
                  <td>{c.brand} {c.model}</td>
                  <td>${p.amountToPay}</td>
                  <td>
                    {new Date(p.rental.rentalDate).toLocaleDateString()} —{' '}
                    {new Date(p.rental.returnDate).toLocaleDateString()}
                  </td>
                  <td
                    className={`status-${p.status.toLowerCase()} clickable`}
                    onClick={() => togglePaymentStatus(p.sessionId)}
                    style={{ cursor: 'pointer' }}
                  >
                    {p.status}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default AdminPayments;
