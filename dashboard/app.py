
import streamlit as st
import subprocess
import json
import base64
import os
import pandas as pd

from pathlib import Path
from datetime import date

# --------------------------------------------------
# PAGE CONFIGURATION
# --------------------------------------------------

st.set_page_config(
    page_title="SalesScope | Retail Analytics",
    page_icon="📊",
    layout="wide"
)

PROJECT_ROOT = Path(__file__).resolve().parent.parent

JAVA_CLASS_PATH = (
        PROJECT_ROOT
        / "out"
        / "production"
        / "capstone-CP020-Bashra-Ayushi"
)

# --------------------------------------------------
# JAVA BACKEND CONNECTION
# --------------------------------------------------

def start_java_backend():

    process = subprocess.Popen(
        [
            "java",
            "-cp",
            str(JAVA_CLASS_PATH),
            "DashboardBridge"
        ],
        cwd=str(PROJECT_ROOT),
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding="utf-8",
        bufsize=1
    )

    return process


if "java_process" not in st.session_state:
    st.session_state.java_process = None

if "backend_error" not in st.session_state:
    st.session_state.backend_error = None


def get_backend():

    process = st.session_state.java_process

    if process is None or process.poll() is not None:

        st.session_state.java_process = start_java_backend()
        process = st.session_state.java_process

        response = send_command("PING", process)

        if response.get("status") != "READY":
            raise RuntimeError("Java backend could not start.")

    return process


def send_command(command, process=None):

    if process is None:
        process = st.session_state.java_process

    if process is None or process.poll() is not None:
        raise RuntimeError("Java backend is not running.")

    process.stdin.write(command + "\n")
    process.stdin.flush()

    response = process.stdout.readline()

    if not response:
        raise RuntimeError("Java backend returned no response.")

    return json.loads(response)


def encode_text(value):

    return base64.b64encode(
        value.encode("utf-8")
    ).decode("ascii")


def backend_command(command):

    try:
        process = get_backend()
        response = send_command(command, process)

        if response.get("status") == "ERROR":
            st.error(response.get("message", "Java operation failed."))

        return response

    except Exception as error:
        st.error(f"Backend connection error: {error}")
        return None


# --------------------------------------------------
# BACKEND INITIALIZATION
# --------------------------------------------------

try:

    process = get_backend()
    backend_ready = True

except Exception as error:

    backend_ready = False
    st.error(f"Could not connect to Java: {error}")


# --------------------------------------------------
# HEADER
# --------------------------------------------------

st.title("📊 SalesScope")

st.subheader("Retail Sales Analytics Dashboard")

st.caption(
    "A Java-powered retail sales management and analytics system"
)

if backend_ready:
    st.success("Java Backend Connected")
else:
    st.error("Java Backend Disconnected")


st.divider()


# --------------------------------------------------
# SIDEBAR NAVIGATION
# --------------------------------------------------

st.sidebar.title("SalesScope")

page = st.sidebar.radio(
    "Navigation",
    [
        "Dashboard Overview",
        "Add Sales Record",
        "View Sales Records",
        "Product Analysis",
        "Category Analysis",
        "Sales Comparison"
    ]
)


# --------------------------------------------------
# COMMON DATA FUNCTIONS
# --------------------------------------------------

def get_records():

    response = backend_command("LIST")

    if response and response.get("status") == "SUCCESS":
        return response.get("records", [])

    return []


def get_dataframe():

    records = get_records()

    if not records:
        return pd.DataFrame(
            columns=[
                "Product",
                "Category",
                "Quantity",
                "Unit Price",
                "Date",
                "Revenue"
            ]
        )

    df = pd.DataFrame(records)

    df = df.rename(
        columns={
            "product": "Product",
            "category": "Category",
            "quantity": "Quantity",
            "unitPrice": "Unit Price",
            "date": "Date",
            "revenue": "Revenue"
        }
    )

    return df


def show_metrics():

    response = backend_command("SUMMARY")

    if not response or response.get("status") != "SUCCESS":
        return

    c1, c2, c3, c4 = st.columns(4)

    c1.metric(
        "Total Revenue",
        f"₹{response['totalRevenue']:,.2f}"
    )

    c2.metric(
        "Sales Records",
        response["totalRecords"]
    )

    c3.metric(
        "Units Sold",
        response["totalQuantity"]
    )

    c4.metric(
        "Categories",
        response["totalCategories"]
    )


# --------------------------------------------------
# 1. DASHBOARD OVERVIEW
# --------------------------------------------------

if page == "Dashboard Overview":

    st.header("Sales Overview")

    show_metrics()

    st.divider()

    df = get_dataframe()

    if not df.empty:

        left, right = st.columns(2)

        with left:

            st.subheader("Revenue by Date")

            date_revenue = (
                df.groupby("Date")["Revenue"]
                .sum()
                .sort_index()
            )

            st.line_chart(date_revenue)

        with right:

            st.subheader("Category-wise Revenue")

            category_revenue = (
                df.groupby("Category")["Revenue"]
                .sum()
                .sort_values(ascending=False)
            )

            st.bar_chart(category_revenue)

        st.subheader("Recent Sales Records")

        st.dataframe(
            df.sort_values("Date", ascending=False).head(10),
            use_container_width=True,
            hide_index=True
        )

    else:

        st.info("No sales records available. Add your first record.")


# --------------------------------------------------
# 2. ADD SALES RECORD
# --------------------------------------------------

elif page == "Add Sales Record":

    st.header("Add New Sales Record")

    with st.form("sales_form"):

        product = st.text_input("Product Name")

        category = st.text_input("Category")

        quantity = st.number_input(
            "Quantity",
            min_value=1,
            step=1
        )

        price = st.number_input(
            "Unit Price (₹)",
            min_value=0.01,
            step=1.0
        )

        sale_date = st.date_input(
            "Sale Date",
            value=date.today()
        )

        submitted = st.form_submit_button(
            "Add Sales Record",
            use_container_width=True
        )

        if submitted:

            if not product.strip() or not category.strip():

                st.error("Product name and category cannot be empty.")

            else:

                command = (
                        "ADD|"
                        + encode_text(product.strip())
                        + "|"
                        + encode_text(category.strip())
                        + "|"
                        + str(quantity)
                        + "|"
                        + str(price)
                        + "|"
                        + sale_date.isoformat()
                )

                response = backend_command(command)

                if response and response.get("status") == "SUCCESS":

                    st.success(response["message"])


# --------------------------------------------------
# 3. VIEW SALES RECORDS
# --------------------------------------------------

elif page == "View Sales Records":

    st.header("All Sales Records")

    df = get_dataframe()

    if not df.empty:

        search = st.text_input("Search Product")

        if search.strip():

            response = backend_command(
                "SEARCH|" + encode_text(search.strip())
            )

            if response and response.get("status") == "SUCCESS":

                results = response.get("records", [])

                if results:

                    result_df = pd.DataFrame(results)

                    result_df = result_df.rename(
                        columns={
                            "product": "Product",
                            "category": "Category",
                            "quantity": "Quantity",
                            "unitPrice": "Unit Price",
                            "date": "Date",
                            "revenue": "Revenue"
                        }
                    )

                    st.dataframe(
                        result_df,
                        use_container_width=True,
                        hide_index=True
                    )

                else:

                    st.warning("Product not found.")

        else:

            st.dataframe(
                df,
                use_container_width=True,
                hide_index=True
            )

        st.metric("Total Records", len(df))

    else:

        st.info("No sales records available.")


# --------------------------------------------------
# 4. PRODUCT ANALYSIS
# --------------------------------------------------

elif page == "Product Analysis":

    st.header("Product Performance Analysis")

    st.subheader("Daily Sales Analysis")

    daily_date = st.date_input(
        "Select a date",
        value=date.today(),
        key="daily_date"
    )

    if st.button("Calculate Daily Revenue"):

        response = backend_command(
            "DAILY|" + daily_date.isoformat()
        )

        if response and response.get("status") == "SUCCESS":

            st.metric(
                "Daily Revenue",
                f"₹{response['revenue']:,.2f}"
            )

    st.divider()

    st.subheader("Weekly Sales Analysis")

    weekly_date = st.date_input(
        "Select any date in the week",
        value=date.today(),
        key="weekly_date"
    )

    if st.button("Calculate Weekly Revenue"):

        response = backend_command(
            "WEEKLY|" + weekly_date.isoformat()
        )

        if response and response.get("status") == "SUCCESS":

            st.write(
                f"Week: {response['weekStart']} to {response['weekEnd']}"
            )

            st.metric(
                "Weekly Revenue",
                f"₹{response['revenue']:,.2f}"
            )

    st.divider()

    st.subheader("Top 5 Products by Revenue")

    if st.button("Show Top Products by Revenue"):

        response = backend_command("TOP_REVENUE")

        if response and response.get("status") == "SUCCESS":

            products = response.get("products", [])

            if products:

                df = pd.DataFrame(products)

                df = df.rename(
                    columns={
                        "product": "Product",
                        "quantity": "Quantity Sold",
                        "revenue": "Revenue"
                    }
                )

                st.bar_chart(
                    df.set_index("Product")["Revenue"]
                )

                st.dataframe(
                    df,
                    use_container_width=True,
                    hide_index=True
                )

            else:

                st.info("No product data available.")

    st.divider()

    st.subheader("Top 5 Products by Quantity")

    if st.button("Show Top Products by Quantity"):

        response = backend_command("TOP_QUANTITY")

        if response and response.get("status") == "SUCCESS":

            products = response.get("products", [])

            if products:

                df = pd.DataFrame(products)

                df = df.rename(
                    columns={
                        "product": "Product",
                        "quantity": "Quantity Sold",
                        "revenue": "Revenue"
                    }
                )

                st.bar_chart(
                    df.set_index("Product")["Quantity Sold"]
                )

                st.dataframe(
                    df,
                    use_container_width=True,
                    hide_index=True
                )

            else:

                st.info("No product data available.")


# --------------------------------------------------
# 5. CATEGORY ANALYSIS
# --------------------------------------------------

elif page == "Category Analysis":

    st.header("Category-wise Sales Analysis")

    if st.button("Analyze Categories"):

        response = backend_command("CATEGORY")

        if response and response.get("status") == "SUCCESS":

            categories = response.get("categories", [])

            if categories:

                df = pd.DataFrame(categories)

                df = df.rename(
                    columns={
                        "category": "Category",
                        "revenue": "Revenue",
                        "percentage": "Percentage"
                    }
                )

                st.metric(
                    "Total Revenue",
                    f"₹{response['totalRevenue']:,.2f}"
                )

                left, right = st.columns(2)

                with left:

                    st.subheader("Revenue Distribution")

                    st.bar_chart(
                        df.set_index("Category")["Revenue"]
                    )

                with right:

                    st.subheader("Category Contribution")

                    st.dataframe(
                        df,
                        use_container_width=True,
                        hide_index=True
                    )

            else:

                st.info("No category data available.")


# --------------------------------------------------
# 6. TWO-WEEK SALES COMPARISON
# --------------------------------------------------

elif page == "Sales Comparison":

    st.header("Two-Week Sales Comparison")

    col1, col2 = st.columns(2)

    with col1:

        date1 = st.date_input(
            "Select any date from Week 1",
            value=date.today(),
            key="compare_date1"
        )

    with col2:

        date2 = st.date_input(
            "Select any date from Week 2",
            value=date.today(),
            key="compare_date2"
        )

    if st.button("Compare Sales", use_container_width=True):

        response = backend_command(
            "COMPARE|"
            + date1.isoformat()
            + "|"
            + date2.isoformat()
        )

        if response and response.get("status") == "SUCCESS":

            c1, c2, c3 = st.columns(3)

            c1.metric(
                "Week 1 Revenue",
                f"₹{response['week1Revenue']:,.2f}"
            )

            c2.metric(
                "Week 2 Revenue",
                f"₹{response['week2Revenue']:,.2f}"
            )

            c3.metric(
                "Revenue Difference",
                f"₹{response['difference']:,.2f}"
            )

            percentage = response.get("percentageChange")

            if percentage is None:

                st.info("Percentage change is unavailable because Week 1 revenue is zero.")

            else:

                st.metric(
                    "Percentage Change",
                    f"{percentage:.2f}%"
                )

            performance = response["performance"]

            if performance == "INCREASED":

                st.success("Sales revenue increased.")

            elif performance == "DECREASED":

                st.warning("Sales revenue decreased.")

            elif performance == "NO_BASELINE":

                st.info("Week 1 has no revenue, so percentage change cannot be calculated.")

            else:

                st.info("Sales revenue remained unchanged.")


# --------------------------------------------------
# FOOTER
# --------------------------------------------------

st.divider()

st.caption(
    "SalesScope | Retail Sales Analytics System | "
    "Java Backend + Python Streamlit"
)
