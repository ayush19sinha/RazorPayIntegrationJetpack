package my.android.razorpayjetpack

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import my.android.razorpayjetpack.ui.theme.RazorPayJetpackTheme
import org.json.JSONObject

class MainActivity : ComponentActivity(), PaymentResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RazorPayJetpackTheme {
                val amountState = remember { mutableStateOf("") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        HeaderText()
                        AmountInputField(amountState)
                        PayButton(amountState)
                    }
                }
            }
        }
    }

    @Composable
    private fun HeaderText() {
        Text(
            text = "Razorpay Jetpack Integration",
            modifier = Modifier.padding(16.dp)
        )
    }

    @Composable
    private fun AmountInputField(amountState: MutableState<String>) {
        BasicTextField(
            value = amountState.value,
            onValueChange = { amountState.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp)),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (amountState.value.isEmpty()) {
                        Text(
                            text = "Enter amount",
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }

    @Composable
    private fun PayButton(amountState: MutableState<String>) {
        Button(
            onClick = {
                handlePaymentButtonClick(amountState)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Pay", color = Color.White)
        }
    }

    private fun handlePaymentButtonClick(amountState: MutableState<String>) {
        val amountText = amountState.value.trim()

        when {
            amountText.isEmpty() -> {
                showToast("Please enter an amount")
                return
            }
            amountText.toIntOrNull()?.let { it <= 0 } ?: true -> {
                showToast("Please enter a valid amount")
                return
            }
            else -> {
                startPayment(amountText.toInt())
                amountState.value = ""
            }
        }
    }

    private fun startPayment(amount: Int) {
        val checkout = Checkout()
        checkout.setKeyID("Your_API_KEY") // Replace this with your actual API key

        try {
            val options = createPaymentOptions(amount)
            checkout.open(this, options)
        } catch (e: Exception) {
            showToast("Error in payment: ${e.message}")
            Log.e("MainActivity", "Error starting payment", e)
        }
    }

    private fun createPaymentOptions(amount: Int): JSONObject {
        return JSONObject().apply {
            put("name", "RazorPay Integration")
            put("description", "Funding Charges")
            put("theme.color", "#3399cc")
            put("currency", "INR")
            put("amount", (amount * 100).toString())
            put("prefill", JSONObject().apply {
                put("email", "Android_14@example.com")
                put("contact", "9876543210")
            })
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        showToast("Payment Successful")
        Log.d("MainActivity", "Payment Successful: $razorpayPaymentId")
    }

    override fun onPaymentError(code: Int, response: String?) {
        showToast("Payment Not Successful")
        Log.e("MainActivity", "Payment error $code: $response")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
