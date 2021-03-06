import "./requests.scss"
import AdminSidebar from "../../components/sidebar/AdminSidebar"
import AdminNavbar from "../../components/navbar/AdminNavbar"
import ReviewTable from "../../components/reviewTable/ReviewTable"

const Reviews = () => {
  return (
    <div className="requestList">
      <AdminSidebar/>
      <div className="requestListContainer">
        <AdminNavbar/>
        <ReviewTable />
      </div>
    </div>
  )
}

export default Reviews