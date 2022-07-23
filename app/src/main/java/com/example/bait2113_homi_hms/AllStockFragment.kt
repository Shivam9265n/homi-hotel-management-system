package com.example.bait2113_homi_hms


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bait2113_homi_hms.adapter.RecyclerAdapter
import com.example.bait2113_homi_hms.objectModel.StockViewModel
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.all_stock2.*
import kotlinx.android.synthetic.main.low_stock2.*
import java.util.*


class AllStockFragment : Fragment(), RecyclerAdapter.OnItemClickListener{

    private lateinit var viewModel: StockViewModel
    private var stockLists = mutableListOf<InventoryModel>()
    private lateinit var adapter: RecyclerAdapter
    private lateinit var reportContext: Context
    private lateinit var recyclerStock: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        var mView: View = inflater.inflate(R.layout.all_stock2, container, false)
        recyclerStock = mView.findViewById<RecyclerView>(R.id.rvAllStock)
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StockViewModel::class.java)
        // TODO: Use the ViewModel
        reportContext = requireContext()
        getTransactionsFromDB()
    }

    private fun getTransactionsFromDB() {
        // model
        var stock: InventoryModel
        // get db reference
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Inventory")
        // controls

        // query value listener
        val queryValueListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val snapshotIterator = dataSnapshot.children
                val iterator: Iterator<DataSnapshot> = snapshotIterator.iterator()
                while (iterator.hasNext()) {
//                    Log.i("Iterator contents:", iterator.toString())
//                    Log.i(TAG, "Value = " + iterator.next().child("id").value)
                    stock = iterator.next().getValue(InventoryModel::class.java) as InventoryModel
                    stockLists.add(stock)
                }
                if (stockLists.size > 0) {
                    adapter = RecyclerAdapter(reportContext, stockLists, this@AllStockFragment)
                    this@AllStockFragment.recyclerStock.adapter = adapter
                    this@AllStockFragment.recyclerStock.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    adapter.itemCount
                    this@AllStockFragment.recyclerStock.setHasFixedSize(true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        // query to get single value (id)
        val query: Query = ref.orderByKey()
        query.addListenerForSingleValueEvent(queryValueListener)

    }
    override fun updateQuantity(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        val mView: View = getLayoutInflater().inflate(R.layout.update_stock, null)
        builder.setTitle(getString(R.string.updateStock))
        val prodName:TextView = mView.findViewById<TextView>(R.id.stockName_lbl)
        prodName.text = stockLists[position].prodName
        val prodQty:EditText = mView.findViewById<EditText>(R.id.prodQty_text_input)
        prodQty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val customisedErrorIcon = resources.getDrawable(R.drawable.error_icon_display) //getDrawable(int, Resources.Theme) instead.

                customisedErrorIcon?.setBounds(
                        0, 0,
                        customisedErrorIcon.intrinsicWidth,
                        customisedErrorIcon.intrinsicHeight
                )

                if (prodQty.text.toString().isEmpty()) {
                    prodQty.setError("Required Field!", customisedErrorIcon)
                    prodQty.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val customisedErrorIcon = resources.getDrawable(R.drawable.error_icon_display) //getDrawable(int, Resources.Theme) instead.

                customisedErrorIcon?.setBounds(
                        0, 0,
                        customisedErrorIcon.intrinsicWidth,
                        customisedErrorIcon.intrinsicHeight
                )

                if (prodQty.text.toString().isEmpty()) {
                    prodQty.setError("Required Field!", customisedErrorIcon)
                    prodQty.requestFocus()
                }}
        })
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            stockLists[position].qty = prodQty.text.toString().toInt()
            val inventoryRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Inventory/")
            inventoryRef.child(stockLists[position].inventoryId).setValue(stockLists[position])
            adapter.onClear()
            getTransactionsFromDB()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->

        }
        builder.setView(mView)
        builder.show()
    }
}

