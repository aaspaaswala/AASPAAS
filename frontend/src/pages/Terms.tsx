import { FileText } from "lucide-react";

const sections = [
  {
    title: "1. Acceptance of Terms",
    content: [
      "AasPaas Wala app ya website use karke aap in terms se agree karte ho.",
      "Agar aap in terms se agree nahi karte toh app/website use mat karo.",
      "Hum kabhi bhi in terms ko update kar sakte hain — major changes pe notify kiya jaayega.",
    ],
  },
  {
    title: "2. Account Registration",
    content: [
      "Account banane ke liye valid mobile number zaroori hai — OTP verification hoga.",
      "Ek mobile number se sirf ek account ban sakta hai.",
      "Aap apne account ki security ke liye responsible ho — kisi ke saath credentials share mat karo.",
      "Galat information dene pe account terminate kiya ja sakta hai.",
    ],
  },
  {
    title: "3. Customer Terms",
    content: [
      "Reservation free hai — koi advance payment nahi.",
      "Reservation 6 ghante ke liye valid hai. Time pe pickup karo.",
      "Agar pickup nahi kiya toh reservation automatically cancel ho jaata hai — koi penalty nahi.",
      "Ek baar reservation confirm hone ke baad store ne product hold kar liya hota hai — please respect karo.",
      "Fake reservations ya spam karne pe account permanently ban ho sakta hai.",
    ],
  },
  {
    title: "4. Business Owner Terms",
    content: [
      "Sirf genuine, existing businesses register kar sakte hain.",
      "Listed products accurate aur available hone chahiye — fake listings allowed nahi hain.",
      "Reservation confirm karne ke baad product hold karna mandatory hai.",
      "Customer ke aane pe reservation code verify karna zaroori hai.",
      "Repeated cancellations ya poor service pe account suspend ho sakta hai.",
      "AasPaas koi commission nahi leta — sirf subscription fee hai.",
    ],
  },
  {
    title: "5. Prohibited Activities",
    content: [
      "Fake accounts banana ya impersonation karna.",
      "Spam reservations karna ya system abuse karna.",
      "Galat product information ya pricing dena.",
      "Dusre users ko harass karna ya inappropriate content post karna.",
      "App ko reverse engineer karna ya security bypass karne ki koshish karna.",
      "Illegal products ya services list karna.",
    ],
  },
  {
    title: "6. Payments & Refunds",
    content: [
      "AasPaas sirf platform provide karta hai — actual payment customer aur store ke beech hoti hai.",
      "AasPaas kisi bhi transaction dispute ke liye responsible nahi hai.",
      "Subscription fees non-refundable hain unless technical issue hamare end pe ho.",
      "Billing disputes ke liye billing@aaspaas.in pe contact karo.",
    ],
  },
  {
    title: "7. Intellectual Property",
    content: [
      "AasPaas naam, logo, aur app design hamare intellectual property hain.",
      "App content ko bina permission copy ya redistribute nahi kar sakte.",
      "User-generated content (reviews, etc.) ke liye aap responsible ho.",
    ],
  },
  {
    title: "8. Limitation of Liability",
    content: [
      "AasPaas ek platform hai — store aur customer ke beech transactions ke liye directly responsible nahi hai.",
      "Product quality, availability, ya store behavior ke liye AasPaas liable nahi hai.",
      "Technical issues ya downtime ke liye maximum liability subscription fee tak limited hai.",
    ],
  },
  {
    title: "9. Termination",
    content: [
      "Aap kabhi bhi account delete kar sakte ho — app settings se.",
      "Terms violation pe hum account suspend ya terminate kar sakte hain.",
      "Account terminate hone pe saara data 30 din mein delete ho jaata hai.",
    ],
  },
  {
    title: "10. Governing Law",
    content: [
      "Yeh terms Indian law ke under governed hain.",
      "Koi bhi dispute Delhi courts mein resolve hoga.",
      "Koi bhi complaint ke liye pehle support@aaspaas.in pe contact karo.",
    ],
  },
];

export default function Terms() {
  return (
    <div className="pt-16">
      <section className="bg-gradient-to-br from-orange-50 via-white to-amber-50 py-20">
        <div className="max-w-3xl mx-auto px-4 text-center">
          <div className="w-14 h-14 bg-brand-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <FileText className="w-7 h-7 text-brand-500" />
          </div>
          <h1 className="text-5xl font-extrabold text-gray-900 mb-4">Terms & Conditions</h1>
          <p className="text-gray-500">Last updated: January 2025</p>
          <p className="text-gray-500 mt-3 text-sm max-w-xl mx-auto">
            AasPaas Wala use karne se pehle yeh terms padho. Yeh aapke aur hamare beech ka agreement hai.
          </p>
        </div>
      </section>

      <section className="py-16 bg-white">
        <div className="max-w-3xl mx-auto px-4 sm:px-6">
          {/* Quick summary */}
          <div className="bg-amber-50 border border-amber-100 rounded-2xl p-5 mb-10">
            <p className="font-semibold text-amber-800 text-sm mb-2">⚡ Quick Summary (TL;DR)</p>
            <ul className="space-y-1 text-xs text-amber-700">
              <li>• Customers: Reservation free hai, 6 ghante mein pickup karo, fake reservations mat karo</li>
              <li>• Business: Sahi products list karo, reservations honor karo, spam mat karo</li>
              <li>• Dono: Respectful raho, platform abuse mat karo</li>
            </ul>
          </div>

          <div className="space-y-10">
            {sections.map((s) => (
              <div key={s.title} className="border-b border-gray-100 pb-10 last:border-0">
                <h2 className="text-xl font-bold text-gray-900 mb-4">{s.title}</h2>
                <ul className="space-y-2">
                  {s.content.map((c, i) => (
                    <li key={i} className="flex items-start gap-3 text-sm text-gray-600 leading-relaxed">
                      <span className="w-1.5 h-1.5 bg-brand-400 rounded-full mt-2 flex-shrink-0" />
                      {c}
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>

          <div className="mt-12 bg-brand-50 rounded-2xl p-6 border border-brand-100">
            <p className="font-semibold text-gray-900 mb-1">Koi sawaal hai terms ke baare mein?</p>
            <p className="text-sm text-gray-500">Email karo: <a href="mailto:legal@aaspaas.in" className="text-brand-500 hover:underline">legal@aaspaas.in</a></p>
          </div>
        </div>
      </section>
    </div>
  );
}
