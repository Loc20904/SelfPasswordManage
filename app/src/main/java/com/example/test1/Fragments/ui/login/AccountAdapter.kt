package com.example.test1.adapters

import Account
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test1.R

class AccountAdapter(
    private val items: List<Account>,
    private val onItemClick: (Account) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvPassword: TextView = itemView.findViewById(R.id.tvPassword)

        fun bind(account: Account) {
            tvAppName.text = account.appName
            tvUsername.text = account.username
            tvPassword.text = "••••••••"

            itemView.setOnClickListener {
                onItemClick(account)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
