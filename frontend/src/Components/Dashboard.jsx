import React from "react";
import VehicleTable from "./VehicleTable";

const Dashboard = () => {
  return (
    <div className="text-white min-h-screen bg-gray-900 p-10">
      <h1 className="text-4xl font-bold">Welcome to AutoLog Dashboard</h1>
      <VehicleTable />
    </div>
  );
};

export default Dashboard;
