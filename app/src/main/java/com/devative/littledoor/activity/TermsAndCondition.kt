package com.devative.littledoor.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityTermsAndConditionBinding

class TermsAndCondition : AppCompatActivity() {
    lateinit var binding:ActivityTermsAndConditionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndConditionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        if (intent.hasExtra("privacyPolicy")){
            binding.toolbar.title = getString(R.string.privacy_policy)
            binding.txtData.text = "Privacy Policy of Little Door\n" +
                    "Introduction\n" +
                    "Welcome to Little Door, your trusted companion for mental health services. This privacy policy outlines how we collect, use, and protect your personal data, in compliance with the Digital Personal Data Protection Act, 2023 (DPDP Act), India.\n" +
                    "\n" +
                    "Scope\n" +
                    "This policy applies to all users within and outside India, in relation to our services offered within India.\n" +
                    "\n" +
                    "Personal Data\n" +
                    "\"Personal data\" means any information that identifies you. We commit to only collecting data necessary for providing our services and not processing it for unrelated purposes.\n" +
                    "\n" +
                    "Roles and Responsibilities\n" +
                    "As a Data Fiduciary, we are responsible for safeguarding your personal data. We ensure that any third-party Data Processors engaged by us also adhere to these standards.\n" +
                    "\n" +
                    "User Rights\n" +
                    "You have the right to access, correct, erase your data, and address grievances. You may also nominate someone to exercise these rights if you're unable to do so.\n" +
                    "\n" +
                    "Child Data Protection\n" +
                    "For users under 18, we require parental consent. We strictly avoid processing data that could harm a child's well-being.\n" +
                    "\n" +
                    "Data Breach Notification\n" +
                    "We commit to promptly notifying the Data Protection Board and you in case of a data breach.\n" +
                    "\n" +
                    "Penalties and Compliance\n" +
                    "We are aware of and adhere to the penalties for non-compliance with the DPDP Act.\n" +
                    "\n" +
                    "Data Transfer Restrictions\n" +
                    "We comply with government guidelines on cross-border data transfer.\n" +
                    "\n" +
                    "Processing Principles\n" +
                    "We process your data based on consent and legitimate needs, ensuring data minimization, accuracy, and storage limitation.\n" +
                    "\n" +
                    "Policy Presentation\n" +
                    "This policy is written in clear, understandable language.\n" +
                    "\n" +
                    "Exemptions and Special Circumstances\n" +
                    "Certain exemptions may apply to our data processing activities, detailed in the full policy.\n" +
                    "\n" +
                    "Ongoing Compliance and Updates\n" +
                    "We regularly review and update this policy.\n" +
                    "\n" +
                    "Contact and Grievance Redressal\n" +
                    "For any queries or concerns regarding your personal data, please contact us at [insert contact information].\n" +
                    "\n" +
                    "Commitment\n" +
                    "At Little Door, your privacy is our priority. We are committed to protecting your personal data and ensuring your peace of mind while using our services."
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}