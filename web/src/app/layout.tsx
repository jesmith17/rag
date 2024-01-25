import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'


const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Rag Optimizer',
  description: 'For use with RAG Architectures to test and validate various configurations and see which changes take effect',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className={`${inter.className} bg-evergreen`}>
          {children}
      </body>
    </html>
  )
}
