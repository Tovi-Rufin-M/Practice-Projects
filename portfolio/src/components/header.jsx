import style from'./styles/header.module.css'

function header(){
    return(
        <header id={style.header}>
            <h3>TR</h3>
            <nav>
                <ul><a href="">Home</a></ul>
                <ul><a href="">About</a></ul>
                <ul><a href="">Projects</a></ul>
                <ul><a href="">Contacts</a></ul>
            </nav>
        </header>
    )
}
export default header