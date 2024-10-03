package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Transactiondetails")
public class Transactiondetails extends HttpServlet {
    private static final long serialVersionUID = 1L;

    String url = "jdbc:mysql://localhost:3306/project";
    String username = "root";
    String password = "admin";
    Connection con = null;
    PreparedStatement pstmtDeposit = null;
    PreparedStatement pstmtWithdraw = null;
    PreparedStatement pstmtTransferFrom = null;
    PreparedStatement pstmtTransferTo = null;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Connection established successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error establishing database connection.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/html");

        String accountNoParam = req.getParameter("accountno");

        if (accountNoParam == null || accountNoParam.isEmpty()) {
            writer.println("<h3 style='color:red;display: flex; justify-content: center; align-items: center; height: 100vh;'>Error: Account number is missing or invalid.</h3>");
            return;  // Exit the method if no account number is provided
        }

        int accountNo = 0;
        try {
            accountNo = Integer.parseInt(accountNoParam);
        } catch (NumberFormatException e) {
            writer.println("<h3 style='color:red;display: flex; justify-content: center; align-items: center; height: 100vh;'>Error: Invalid account number format.</h3>");
            return;
        }

        try {
            // Fetch deposit transactions
            String queryDeposit = "SELECT * FROM depositmoney WHERE accountno = ?";
            pstmtDeposit = con.prepareStatement(queryDeposit);
            pstmtDeposit.setInt(1, accountNo);
            ResultSet rsDeposit = pstmtDeposit.executeQuery();

            writer.println("<h2>Deposit Transactions</h2>");
            if (!rsDeposit.isBeforeFirst()) {
                writer.println("<p style='display: flex; justify-content: center; align-items: center; height: 100vh;'>No deposits found for account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1'style='display: flex; justify-content: center; align-items: center; height: 100vh;'><tr><th>depositid</th><th>Accountno</th><th>Amount</th><th>deposittime</th></tr>");
                while (rsDeposit.next()) {
                    writer.println("<tr><td>" + rsDeposit.getInt("depositid") + "</td>");
                    writer.println("<td>" + rsDeposit.getInt("accountno") + "</td>");
                    writer.println("<td>" + rsDeposit.getInt("amount") + "</td>");
                    writer.println("<td>" + rsDeposit.getDate("deposittime") + "</td></tr>");
                }
                writer.println("</table>");
            }

            // Fetch withdrawal transactions
            String queryWithdraw = "SELECT * FROM withdrawmoney WHERE accountno = ?";
            pstmtWithdraw = con.prepareStatement(queryWithdraw);
            pstmtWithdraw.setInt(1, accountNo);
            ResultSet rsWithdraw = pstmtWithdraw.executeQuery();

            writer.println("<h2 style='display: flex; justify-content: center; align-items: center; height: 100vh;'>Withdrawal Transactions</h2>");
            if (!rsWithdraw.isBeforeFirst()) {
                writer.println("<p style='display: flex; justify-content: center; align-items: center; height: 100vh;'>No withdrawals found for account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1' style='display: flex; justify-content: center; align-items: center; height: 100vh;'><tr><th>withdrawid</th><th>Accountno</th><th>Amount</th><th>withdrawtime</th></tr>");
                while (rsWithdraw.next()) {
                    writer.println("<tr><td>" + rsWithdraw.getInt("withdrawid") + "</td>");
                    writer.println("<td>" + rsWithdraw.getInt("accountno") + "</td>");
                    writer.println("<td>" + rsWithdraw.getInt("amount") + "</td>");
                    writer.println("<td>" + rsWithdraw.getDate("withdrawtime") + "</td></tr>");
                }
                writer.println("</table>");
            }

            // Fetch money transferred from this account
            String queryTransferFrom = "SELECT * FROM transfermoney WHERE fromaccountno = ?";
            pstmtTransferFrom = con.prepareStatement(queryTransferFrom);
            pstmtTransferFrom.setInt(1, accountNo);
            ResultSet rsTransferFrom = pstmtTransferFrom.executeQuery();

            writer.println("<h2 style='display: flex; justify-content: center; align-items: center; height: 100vh;'>Money Transferred From Account</h2>");
            if (!rsTransferFrom.isBeforeFirst()) {
                writer.println("<p style='display: flex; justify-content: center; align-items: center; height: 100vh;'>No money transfers from account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1' style='display: flex; justify-content: center; align-items: center; height: 100vh;'><tr><th>Transferid</th><th>Fromaccount</th><th>Toaccount</th><th>transfertime</th><th>Amount</th></tr>");
                while (rsTransferFrom.next()) {
                    writer.println("<tr><td>" + rsTransferFrom.getInt("transferid") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getInt("fromaccountno") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getInt("toaccountno") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getDate("transfertime") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getInt("amount") + "</td></tr>");
                }
                writer.println("</table>");
            }

            // Fetch money transferred to this account
            String queryTransferTo = "SELECT * FROM transfermoney WHERE toaccountno = ?";
            pstmtTransferTo = con.prepareStatement(queryTransferTo);
            pstmtTransferTo.setInt(1, accountNo);
            ResultSet rsTransferTo = pstmtTransferTo.executeQuery();

            writer.println("<h2 style='display: flex; justify-content: center; align-items: center; height: 100vh;'>Money Transferred To Account</h2>");
            if (!rsTransferTo.isBeforeFirst()) {
                writer.println("<p style='display: flex; justify-content: center; align-items: center; height: 100vh;'>No money transfers to account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1' style='display: flex; justify-content: center; align-items: center; height: 100vh;'><tr><th>Transferid</th><th>Fromaccount</th><th>Toaccount</th><th>transfertime</th><th>Amount</th></tr>");
                while (rsTransferTo.next()) {
                    writer.println("<tr><td>" + rsTransferTo.getInt("transferid") + "</td>");
                    writer.println("<td>" + rsTransferTo.getInt("fromaccountno") + "</td>");
                    writer.println("<td>" + rsTransferTo.getInt("toaccountno") + "</td>");
                    writer.println("<td>" + rsTransferTo.getDate("transfertime") + "</td>");
                    writer.println("<td>" + rsTransferTo.getInt("amount") + "</td></tr>");
                }
                writer.println("</table>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            writer.println("<h3 style='display: flex; justify-content: center; align-items: center; height: 100vh;'>Error fetching transaction details. Please try again later.</h3>");
        }
    }

    @Override
    public void destroy() {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
