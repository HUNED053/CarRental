import React, { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import AuthContext from "../context/AuthContext";
import { getAllCars } from "../services/carService";
import "../App.css";

const Home = () => {
  const { user } = useContext(AuthContext);
  const [cars, setCars] = useState([]);
  const [selectedImage, setSelectedImage] = useState(null);

  useEffect(() => {
    const fetchCars = async () => {
      try {
        const data = await getAllCars();
        setCars(data);
      } catch (error) {
        console.error("Error fetching cars:", error);
      }
    };

    fetchCars();
  }, []);

  return (
    <div>
      <Navbar />
      <div className="home-container">
        {user && user.firstName && (
          <div className="greeting">
            <p>Hi, {user.firstName}</p>
          </div>
        )}

        <h1>Rent Your Dream Car</h1>
        <p>Choose from a variety of cars at the best prices!</p>

        <div className="car-grid">
          {cars.map((car) => (
            <div key={car.id} className={`car-card ${car.inventory > 0 ? "" : "not-available"}`}>
              <img
                src={`http://localhost:8080${car.carImage}`}
                alt={`${car.brand} ${car.model}`}
                className="car-image"
                onClick={() => setSelectedImage(`http://localhost:8080${car.carImage}`)}
              />
              <h3>{car.brand} {car.model}</h3>
              <p><strong>Type:</strong> {car.type}</p>
              <p><strong>Daily Fee:</strong> ${car.dailyFee}/day</p>
              <p>
                <strong>Available:</strong> 
                <span className={car.inventory > 0 ? "available-text" : "not-available-text"}>
                  {car.inventory > 0 ? " In Stock" : " Out of Stock"}
                </span>
              </p>

              <div className="car-actions">
                {car.inventory > 0 && (
                  <Link to={`/rent-car/${car.id}`} className="btn rent-btn">
                    Rent Car
                  </Link>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Modal for full image */}
      {selectedImage && (
        <div className="modal" onClick={() => setSelectedImage(null)}>
          <span className="modal-close" onClick={() => setSelectedImage(null)}>&times;</span>
          <img src={selectedImage} alt="Car full view" />
        </div>
      )}
    </div>
  );
};

export default Home;
