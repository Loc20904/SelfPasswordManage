package com.example.test1.Fragments.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.test1.MainActivity
import com.example.test1.databinding.FragmentFullscreenBinding
import com.example.test1.R
/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenFragment : Fragment() {

    private var _binding: FragmentFullscreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFullscreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnChangePin = view.findViewById<Button>(R.id.btnChangePin)
        btnChangePin.setOnClickListener {
            (activity as? MainActivity)?.GoToPinChange()
        }

        val btnMange = view.findViewById<Button>(R.id.btnManagePasswords)
        btnMange.setOnClickListener {
            (activity as? MainActivity)?.GoToView()
        }

        val btnAdd = view.findViewById<Button>(R.id.btnAddPassword)
        btnAdd.setOnClickListener {
            (activity as? MainActivity)?.GoToAdd()
        }

        val btnAuto = view.findViewById<Button>(R.id.btnAutoFillSettings)
        btnAuto.setOnClickListener {
            Toast.makeText(requireContext(), "Tính năng đang phát triển.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}