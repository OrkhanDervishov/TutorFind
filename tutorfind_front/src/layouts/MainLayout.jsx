import { Outlet } from "react-router-dom";
import Header from "../components/Header";

const MainLayout = () => {
  return (
    <div className="app-shell">
      <Header />
      <main className="page-content">
        <Outlet />
      </main>
    </div>
  );
};

export default MainLayout;
