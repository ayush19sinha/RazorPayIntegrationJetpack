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

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Text(
                            text = "Razorpay Jetpack Integration",
                            modifier = Modifier.padding(16.dp)
                        )

                        BasicTextField(
                            value = amountState.value,
                            onValueChange = { amountState.value = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(4.dp)),
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



                        Button(
                            onClick = {
                                val amountText = amountState.value.trim()

                                if (amountText.isEmpty()) {
                                    Toast.makeText(this@MainActivity, "Please enter an amount", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val amount = amountText.toIntOrNull()
                                if (amount == null || amount <= 0) {
                                    Toast.makeText(this@MainActivity, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                startPayment(amount)
                                amountState.value = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(text = "Pay", color = Color.White)
                        }
                    }
                }
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
            Toast.makeText(this, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
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
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        Log.d("MainActivity", "Payment Successful: $razorpayPaymentId")
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Not Successful", Toast.LENGTH_SHORT).show()
        Log.e("MainActivity", "Payment error $code: $response")
    }
}
