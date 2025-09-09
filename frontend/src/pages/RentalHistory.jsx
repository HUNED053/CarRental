import React, { useEffect, useState, useContext } from "react";
import AuthContext from "../context/AuthContext";
import { getUserRentals, returnRental } from "../services/rentalService";
import { makePayment } from "../services/paymentService";
import Navbar from "../components/Navbar";
import { toast } from "react-toastify";
import { getAllCars } from "../services/carService";
import { getUserPayments } from "../services/paymentService";

const RentalHistory = () => {
  const { user } = useContext(AuthContext);
  const [rentals, setRentals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isActive, setIsActive] = useState(true);
  const [selectedImage, setSelectedImage] = useState(null);
  const [cars, setCars] = useState([]);
  const [paymentsMap, setPaymentsMap] = useState({});

  useEffect(() => {
    const fetchRentals = async () => {
      try {
        const rentalsData = await getUserRentals(user.id, isActive);
        const carsData = await getAllCars();
        const paymentsData = await getUserPayments(user.id);

        // Create a map of rentalId -> payment status
        const paymentMap = {};
        paymentsData.forEach(payment => {
          if (!paymentMap[payment.rentalId] || payment.status === "PAID") {
            paymentMap[payment.rentalId] = payment.status;
          }
        });

        setPaymentsMap(paymentMap);
        setRentals(rentalsData);
        setCars(carsData);
      } catch (error) {
        console.error("Error fetching rental history:", error);
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      fetchRentals();
    }
  }, [user, isActive]);

  const handleMakePayment = async (rentalId) => {
    try {
      const response = await makePayment({ rentalId, type: "PAYMENT" });
      window.location.href = response.sessionUrl; // Redirect to the existing payment session
    } catch (error) {
      toast.error("Failed to initiate payment.");
    }
  };

  const handleReturnCar = async (rentalId) => {
    try {
      await returnRental(rentalId);
      toast.success("Car returned successfully!");
      setRentals(
        rentals.map((rental) =>
          rental.id === rentalId
            ? { ...rental, actualReturnDate: new Date().toISOString() }
            : rental
        )
      );
    } catch (error) {
      toast.error("Failed to return car.");
    }
  };

  if (loading) return <p className="loading">Loading rental history...</p>;

  return (
    <div>
      <Navbar />
      <div className="rental-history-container">
        <h1>Your Rentals</h1>

        <div className="filter-container">
          <label>Show: </label>
          <select
            value={isActive}
            onChange={(e) => setIsActive(e.target.value === "true")}
          >
            <option value="true">Active Rentals</option>
            <option value="false">Past Rentals</option>
          </select>
        </div>

        {rentals.length === 0 ? (
          <p className="no-rentals">No rentals found.</p>
        ) : (
          <div className="rental-list">
            {rentals.map((rental) => {
              const car = cars.find((c) => c.id === rental.carId);

              return (
                <div key={rental.id} className="rental-card">
                  {car && (
                    <>
                      <img
                        src={`http://localhost:8080${car.carImage}`}
                        alt={`${car.brand} ${car.model}`}
                        className="car-image"
                        onClick={() =>
                          setSelectedImage(
                            `http://localhost:8080${car.carImage}`
                          )
                        }
                      />
                      <h3>
                        {car.brand} {car.model}
                      </h3>
                    </>
                  )}

                  <p>
                    <strong>Rental Start:</strong>{" "}
                    {new Date(rental.rentalDate).toLocaleString()}
                  </p>
                  <p>
                    <strong>Return Date:</strong>{" "}
                    {new Date(rental.returnDate).toLocaleString()}
                  </p>
                  <p>
                    <strong>Actual Return Date:</strong>{" "}
                    {rental.actualReturnDate
                      ? new Date(rental.actualReturnDate).toLocaleString()
                      : "Not Returned Yet"}
                  </p>

                  <div className="rental-actions">
                    {!rental.actualReturnDate && (
                      <>
                        {(!paymentsMap[rental.id] ||
                          paymentsMap[rental.id] === "PENDING") && (
                          <button
                            className="btn pay-btn"
                            onClick={() => handleMakePayment(rental.id)}
                          >
                            Make Payment
                          </button>
                        )}
                        <button
                          className="btn return-btn"
                          onClick={() => handleReturnCar(rental.id)}
                        >
                          Return Car
                        </button>
                      </>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {selectedImage && (
        <div className="modal" onClick={() => setSelectedImage(null)}>
          <span className="modal-close" onClick={() => setSelectedImage(null)}>
            &times;
          </span>
          <img src={selectedImage} alt="Car full view" />
        </div>
      )}
    </div>
  );
};

export default RentalHistory;
