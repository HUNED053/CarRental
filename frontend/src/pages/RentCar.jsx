import React, { useState, useEffect, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import AuthContext from "../context/AuthContext";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { getCarById } from "../services/carService";
import { rentCar } from "../services/rentalService";
import { getDiscountEstimate } from "../services/paymentService";
import { toast } from "react-toastify";
import Navbar from "../components/Navbar";
import "../App.css";

// Yup validation schema
const schema = yup.object().shape({
  rentalDate: yup
    .date()
    .required("Rental date is required")
    .min(
      new Date(new Date().setHours(0, 0, 0, 0)),
      "Rental date must be today or later"
    )
    .max(
      new Date(new Date().setDate(new Date().getDate() + 7)),
      "Rental date must be within 7 days from today"
    ),
  returnDate: yup
    .date()
    .required("Return date is required")
    .min(yup.ref("rentalDate"), "Return date must be after rental date")
    .test(
      "max-45-days",
      "Return date must be within 45 days of rental date",
      function (value) {
        const { rentalDate } = this.parent;
        if (!rentalDate || !value) return true;
        return new Date(value) - new Date(rentalDate) <= 45 * 24 * 60 * 60 * 1000;
      }
    ),
});

const RentCar = () => {
  const { id } = useParams();
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const [car, setCar] = useState(null);
  const [estimate, setEstimate] = useState(null);
  const [loading, setLoading] = useState(false);

  // Load car details
  useEffect(() => {
    getCarById(id)
      .then(setCar)
      .catch(() => toast.error("Error fetching car details."));
  }, [id]);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({ resolver: yupResolver(schema) });

  const rentalDateValue = watch("rentalDate");
  const returnDateValue = watch("returnDate");

  // Fetch discount estimate whenever user, car, or dates change
  useEffect(() => {
    if (user && car && rentalDateValue && returnDateValue) {
      getDiscountEstimate({
        userId: user.id,
        carId: car.id,
        rentalDate: rentalDateValue,
        returnDate: returnDateValue,
      })
        .then(setEstimate)
        .catch(() => setEstimate(null));
    } else {
      setEstimate(null);
    }
  }, [user, car, rentalDateValue, returnDateValue]);

  // Helper to format for backend
  const toLocalDateTime = (dateStr) =>
    new Date(dateStr).toISOString().split(".")[0];

  const onSubmit = async (data) => {
    setLoading(true);
    const payload = {
      carId: parseInt(id, 10),
      rentalDate: toLocalDateTime(data.rentalDate),
      returnDate: toLocalDateTime(data.returnDate),
      userId: user.id,
    };
    try {
      await rentCar(payload);
      toast.success("Car rented successfully!");
      navigate("/");
    } catch {
      toast.error("Failed to rent car.");
    } finally {
      setLoading(false);
    }
  };

  if (!car) return <p className="loading">Loading...</p>;

  return (
    <div>
      <Navbar />
      <div className="form-container">
        <div className="form-box">
          <h2>
            Rent {car.brand} {car.model}
          </h2>
          {car.inventory > 0 ? (
            <form onSubmit={handleSubmit(onSubmit)}>
              {/* Date inputs */}
              <div className="input-group">
                <label>Rental Start Date</label>
                <input
                  type="date"
                  {...register("rentalDate")}
                  min={new Date().toISOString().split("T")[0]}
                  max={new Date(
                    new Date().setDate(new Date().getDate() + 7)
                  )
                    .toISOString()
                    .split("T")[0]}
                />
                <p className="error">{errors.rentalDate?.message}</p>
              </div>
              <div className="input-group">
                <label>Return Date</label>
                <input
                  type="date"
                  {...register("returnDate")}
                  min={new Date().toISOString().split("T")[0]}
                  max={new Date(
                    new Date().getTime() + 45 * 24 * 60 * 60 * 1000
                  )
                    .toISOString()
                    .split("T")[0]}
                />
                <p className="error">{errors.returnDate?.message}</p>
              </div>

              {/* Show back-end estimated discount */}
              {estimate && (
                <>
                  <div className="input-group">
                    <strong>Original Total:</strong> ${estimate.originalAmount}
                  </div>
                  <div className="input-group">
                    <strong>Discount:</strong> {estimate.discountPercent}% (−$
                    {estimate.discountAmount})
                  </div>
                  <div className="input-group">
                    <strong>Total after Discount:</strong> $
                    {estimate.totalAfterDiscount}
                  </div>
                </>
              )}

              <button type="submit" className="btn" disabled={loading}>
                {loading ? "Processing..." : "Confirm Rental"}
              </button>
            </form>
          ) : (
            <p className="not-available-text">Car is Out of Stock</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default RentCar;
