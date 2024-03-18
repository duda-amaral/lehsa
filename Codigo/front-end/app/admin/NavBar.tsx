'use client'

import React from 'react'
import Image from 'next/image'
import Link from 'next/link'
import { usePathname } from 'next/navigation'

const NavBar = () => {
    const pathname = usePathname()

    return (
        <aside className='bg-white h-screen space-y-10 w-52 py-6'>
            <Image src='/logo.jpg' width={150} height={0} alt='logo' className='m-auto'></Image>
            <div className='flex flex-col gap-2 pl-5'>
                <Link href='/admin' className={`pl-6 py-2 rounded-l-full ${pathname === '/admin' ? 'bg-primary text-white' : ''}`}>Dashboard</Link>
                <Link href='/admin/itens' className={`pl-6 py-2 rounded-l-full ${pathname === '/admin/itens' ? 'bg-primary text-white' : ''}`}>Itens</Link>
                <Link href='/admin/agendamentos' className={`pl-6 py-2 rounded-l-full ${pathname === '/admin/agendamentos' ? 'bg-primary text-white' : ''}`}>Agendamentos</Link>
                <Link href='/admin/usuarios' className={`pl-6 py-2 rounded-l-full ${pathname === '/admin/usuarios' ? 'bg-primary text-white' : ''}`}>Usuários</Link>
            </div>
        </aside>
    )
}

export default NavBar